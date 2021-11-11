package uz.devops.rpc4rj.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uz.devops.rpc4rj.annotation.RpcMethod;
import uz.devops.rpc4rj.annotation.RpcParam;
import uz.devops.rpc4rj.annotation.RpcService;
import uz.devops.rpc4rj.error.exception.InvalidParamsException;
import uz.devops.rpc4rj.error.exception.InvalidWrapperException;
import uz.devops.rpc4rj.error.exception.MethodParamsInfoResolveException;
import uz.devops.rpc4rj.model.JsonRpcParamInfo;
import uz.devops.rpc4rj.model.JsonRpcRequest;
import uz.devops.rpc4rj.model.JsonRpcResponse;
import uz.devops.rpc4rj.model.JsonRpcServiceInfo;

import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class ResolverUtil {

    private final ApplicationContext context;
    private final ObjectMapper objectMapper;

    public String[] getRpcUris() {
        log.trace("getRpcUris started");
        return getRpcImplTypes()
                .stream()
                .map(service -> service.getAnnotation(RpcService.class).value())
                .toArray(String[]::new);
    }

    public List<JsonRpcServiceInfo> getRpcInfoList() {
        log.trace("getRpcInfoList started");
        return getRpcImplTypes()
                .stream()
                .map(this::getRpcInfoListByMethod)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private Set<Class<?>> getRpcImplTypes() {
        log.trace("getRpcImplTypes started");
        return new Reflections(getConfigurationBuilder())
                .getTypesAnnotatedWith(RpcService.class);
    }

    private ConfigurationBuilder getConfigurationBuilder() {
        log.trace("getConfigurationBuilder started");
        return new ConfigurationBuilder()
                .setScanners(new TypeAnnotationsScanner())
                // TODO: 11-Nov-21 change package scan to common solution
                .setUrls(ClasspathHelper.forPackage("uz.devops"))
                .filterInputsBy(new FilterBuilder().includePackage("uz.devops"));
    }

    public List<JsonRpcServiceInfo> getRpcInfoListByMethod(Class<?> service) {
        log.trace("getJsonRpcInfo started");

        var bean = context.getBean(service);

        return Arrays
                .stream(service.getDeclaredMethods())
                .filter(
                        method ->
                                Arrays
                                        .stream(method.getDeclaredAnnotations())
                                        .anyMatch(annotation -> annotation.annotationType().equals(RpcMethod.class))
                )
                .map(method -> getJsonRpcInfo(bean, method, service))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public JsonRpcServiceInfo getJsonRpcInfo(Object bean, Method method, Class<?> service) {
        log.trace("getJsonRpcInfo started for bean with name : {}", bean.getClass().getSimpleName());
        var methodAnnotation = getMethodAnnotation(method);
        var params = getParamInfo(method);
        return methodAnnotation == null
                ? null
                : new JsonRpcServiceInfo(service.getAnnotation(RpcService.class).value(), bean, methodAnnotation.value(), method, params);
    }

    public RpcMethod getMethodAnnotation(Method method) {
        log.trace("getAnnotation started");
        return (RpcMethod) Arrays
                .stream(method.getDeclaredAnnotations())
                .filter(annotation1 -> annotation1.annotationType().equals(RpcMethod.class))
                .findFirst()
                .orElse(null);
    }

    public List<JsonRpcParamInfo> getParamInfo(Method method) {
        log.trace("getAnnotation started");
        return Arrays
                .stream(method.getParameters())
                .map(parameter -> new JsonRpcParamInfo(getParamName(parameter), parameter.getType(), parameter.getName()))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private String getParamName(Parameter parameter) {
        log.trace("getParamName started for param with type : {}", parameter.getType());
        return Arrays
                .stream(parameter.getAnnotations())
                .filter(annotation -> annotation.annotationType().equals(RpcParam.class))
                .findFirst()
                .map(annotation -> ((RpcParam) annotation).value())
                .orElseThrow(MethodParamsInfoResolveException::new);
    }

    public boolean isDesiredMethod(@NotNull JsonRpcRequest request, JsonRpcServiceInfo rpcInfo) {
        log.trace("isDesiredMethod started for request method : {}, system method : {}", request.getMethod(), rpcInfo.getMethodName());
        return rpcInfo.getMethodName().equalsIgnoreCase(request.getMethod());
    }

    public Object executeMethod(JsonRpcRequest request, JsonRpcServiceInfo jsonRpcServiceInfo)
            throws IllegalAccessException, InvocationTargetException, InvalidParamsException {
        log.trace(
                "executeMethod started for method : {}, of type : {}",
                jsonRpcServiceInfo.getMethodName(),
                jsonRpcServiceInfo.getInstance().getClass().getSimpleName()
        );

        var params = ((LinkedHashMap<String, ?>) request.getParams());

        if (params.size() != jsonRpcServiceInfo.getParams().size()) {
            throw new InvalidParamsException();
        }

        var args = jsonRpcServiceInfo
                .getParams()
                .stream()
                .sorted(Comparator.comparing(JsonRpcParamInfo::getOrder))
                .map(param -> getParam(params, param))
                .toArray();

        return jsonRpcServiceInfo.getMethod().invoke(jsonRpcServiceInfo.getInstance(), args);
    }

    @SneakyThrows
    private Object getParam(LinkedHashMap<String, ?> params, JsonRpcParamInfo param) {
        log.trace("getParam started");

        if (params.get(param.getName()) == null) {
            throw new InvalidParamsException();
        }

        return objectMapper.convertValue(params.get(param.getName()), param.getType());
    }

    public Mono<ResponseEntity<?>> mapResult(JsonRpcRequest request, JsonRpcServiceInfo jsonRpcServiceInfo, Object result)
            throws InvalidWrapperException {
        log.trace("mapResult started result of method : {}", jsonRpcServiceInfo.getMethodName());

        if (jsonRpcServiceInfo.getMethod().getReturnType().equals(Mono.class)) {
            log.trace("result is Mono type");
            return ((Mono<?>) result).map(response -> ResponseEntity.ok(mapToJsonRpcResponse(request, response)));
        }

        if (jsonRpcServiceInfo.getMethod().getReturnType().equals(Flux.class)) {
            log.trace("result is streaming type (Flux)");
            return Mono.just(ResponseEntity.ok(((Flux<?>) result).map(response -> mapToJsonRpcResponse(request, response))));
        }

        throw new InvalidWrapperException();
    }

    private JsonRpcResponse mapToJsonRpcResponse(JsonRpcRequest request, Object response) {
        log.trace("mapToJsonRpcResponse started");
        return new JsonRpcResponse(request.getId(), "2.0", response, null);
    }
}
