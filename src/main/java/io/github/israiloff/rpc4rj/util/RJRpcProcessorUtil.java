package io.github.israiloff.rpc4rj.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.israiloff.rpc4rj.annotation.RJRpcError;
import io.github.israiloff.rpc4rj.annotation.RJRpcErrors;
import io.github.israiloff.rpc4rj.annotation.RJRpcMethod;
import io.github.israiloff.rpc4rj.annotation.RJRpcParam;
import io.github.israiloff.rpc4rj.annotation.RJRpcService;
import io.github.israiloff.rpc4rj.error.exception.InvalidParamsException;
import io.github.israiloff.rpc4rj.error.exception.InvalidWrapperException;
import io.github.israiloff.rpc4rj.error.exception.JsonRpcVersionValidationException;
import io.github.israiloff.rpc4rj.error.exception.MethodParamsMetaDataException;
import io.github.israiloff.rpc4rj.model.JsonRpcErrorInfo;
import io.github.israiloff.rpc4rj.model.JsonRpcParamInfo;
import io.github.israiloff.rpc4rj.model.JsonRpcRequest;
import io.github.israiloff.rpc4rj.model.JsonRpcResponse;
import io.github.israiloff.rpc4rj.model.JsonRpcServiceInfo;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class RJRpcProcessorUtil {

    public static final ArrayList<JsonRpcErrorInfo> EMPTY_ERROR_LIST = new ArrayList<>();
    public static final String JSON_RPC_VERSION = "2.0";
    private final ApplicationContext context;
    private final ObjectMapper objectMapper;

    public void validateRequest(JsonRpcRequest request) throws JsonRpcVersionValidationException {
        log.trace("validateRequest started");

        if (Objects.equals(request.getJsonrpc(), JSON_RPC_VERSION)) {
            return;
        }

        log.trace("RPC version validation error occurred");
        throw new JsonRpcVersionValidationException(JSON_RPC_VERSION);
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

    public List<JsonRpcServiceInfo> getRpcInfoListByMethod(Class<?> service) {
        log.trace("getJsonRpcInfo started");

        var bean = context.getBean(service);

        return Arrays
                .stream(service.getDeclaredMethods())
                .filter(method -> method.getAnnotation(RJRpcMethod.class) != null)
                .map(method -> getJsonRpcInfo(bean, method, service))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public JsonRpcServiceInfo getJsonRpcInfo(Object bean, Method method, Class<?> service) {
        log.trace("getJsonRpcInfo started for bean with name : {}", bean.getClass().getSimpleName());
        var methodAnnotation = method.getAnnotation(RJRpcMethod.class);
        var errorInfos = getErrorInfos(method);
        var params = getParamInfo(method);
        return methodAnnotation == null
                ? null
                : new JsonRpcServiceInfo(service.getAnnotation(RJRpcService.class).value(),
                bean, methodAnnotation.value(), method, params, errorInfos);
    }

    public List<JsonRpcErrorInfo> getErrorInfos(Method method) {
        log.trace("getErrorInfos started");
        var errors = method.getAnnotation(RJRpcErrors.class);
        if (errors == null) {
            return EMPTY_ERROR_LIST;
        }
        return Arrays.stream(errors.value())
                .map(this::getJsonRpcErrorInfo)
                .collect(Collectors.toList());
    }

    public List<JsonRpcParamInfo> getParamInfo(Method method) {
        log.trace("getAnnotation started");
        return Arrays
                .stream(method.getParameters())
                .map(parameter -> new JsonRpcParamInfo(getParamName(parameter), parameter.getType(), parameter.getName()))
                .collect(Collectors.toList());
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

        var methodParams = jsonRpcServiceInfo.getMethod().getParameterTypes();

        validateMethodParams(args, methodParams);

        return jsonRpcServiceInfo.getMethod().invoke(jsonRpcServiceInfo.getInstance(), args);
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

    private Set<Class<?>> getRpcImplTypes() {
        log.trace("getRpcImplTypes started");
        return new Reflections(getConfigurationBuilder())
                .getTypesAnnotatedWith(RJRpcService.class);
    }

    private ConfigurationBuilder getConfigurationBuilder() {
        log.trace("getConfigurationBuilder started");
        return new ConfigurationBuilder()
                .setScanners(new TypeAnnotationsScanner())
                .setUrls(ClasspathHelper.forJavaClassPath());
    }

    private JsonRpcErrorInfo getJsonRpcErrorInfo(RJRpcError rpcError) {
        log.trace("getJsonRpcErrorInfo");
        return new JsonRpcErrorInfo(rpcError.exception(), rpcError.code(), getOptionalString(rpcError.message()), getOptionalString(rpcError.data()));
    }

    private String getOptionalString(String text) {
        log.trace("getOptionalString started for : {}", text);
        return Objects.equals(text, "") ? null : text;
    }

    @SneakyThrows
    private String getParamName(Parameter parameter) {
        log.trace("getParamName started for param with type : {}", parameter.getType());
        var rpcParam = parameter.getAnnotation(RJRpcParam.class);

        if (rpcParam == null) {
            log.trace("method parameter name annotation is not set");
            throw new MethodParamsMetaDataException();
        }

        return rpcParam.value();
    }

    private void validateMethodParams(Object[] args, Class<?>[] methodParams) throws InvalidParamsException {
        if (args.length != methodParams.length) {
            log.warn("given parameters count not match to target method's. execution terminated");
            throw new InvalidParamsException();
        }


        for (var index = 0; index < args.length; index++) {
            if (args[index] != null && !args[index].getClass().equals(methodParams[index])) {
                log.warn("one of method params type is invalid. request arg - '{}' : method arg type - {}", args[index], methodParams[index]);
                throw new InvalidParamsException();
            }
        }
    }

    @SneakyThrows
    private Object getParam(LinkedHashMap<String, ?> params, JsonRpcParamInfo param) {
        log.trace("getParam started");

        if (params.get(param.getName()) == null) {
            throw new InvalidParamsException();
        }

        return objectMapper.convertValue(params.get(param.getName()), param.getType());
    }

    private JsonRpcResponse mapToJsonRpcResponse(JsonRpcRequest request, Object response) {
        log.trace("mapToJsonRpcResponse started");
        return new JsonRpcResponse(request.getId(), "2.0", response, null);
    }
}
