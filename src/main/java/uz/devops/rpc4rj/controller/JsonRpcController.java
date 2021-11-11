package uz.devops.rpc4rj.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import uz.devops.rpc4rj.model.JsonRpcRequest;
import uz.devops.rpc4rj.model.JsonRpcServiceInfo;
import uz.devops.rpc4rj.model.RpcServiceMetaData;
import uz.devops.rpc4rj.service.JsonRpcProcessor;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;

@Slf4j
@RestController
@RequiredArgsConstructor
public class JsonRpcController {

    public static final String REACTIVE_REQUEST_KEY = "reactiveRequest";

    private final RequestMappingHandlerMapping handlerMapping;
    private final RpcServiceMetaData metaData;
    private final JsonRpcProcessor processor;

    @PostConstruct
    public void init() {
        handlerMapping.registerMapping(
                RequestMappingInfo
                        .paths(metaData.getRpcInfoList().stream().map(JsonRpcServiceInfo::getUri).toArray(String[]::new))
                        .methods(RequestMethod.POST)
                        .produces(MediaType.APPLICATION_STREAM_JSON_VALUE)
                        .build(),
                this,
                getEndpoint());
    }

    public Mono<ResponseEntity<?>> endpoint(@Valid @RequestBody @NotNull JsonRpcRequest request,
                                            ServerWebExchange exchange) throws Exception {
        log.debug("RPC request to common rpc endpoint. uri : {}, body : {}", exchange.getRequest().getPath().value(), request);
        exchange.getAttributes().put(REACTIVE_REQUEST_KEY, request);
        return processor.process(request, exchange.getRequest().getPath().value());
    }

    @SneakyThrows
    private Method getEndpoint() {
        log.trace("getEndpoint started");
        return JsonRpcController.class.getDeclaredMethod("endpoint", JsonRpcRequest.class, ServerWebExchange.class);
    }
}
