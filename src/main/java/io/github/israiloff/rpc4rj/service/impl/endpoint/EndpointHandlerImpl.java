package io.github.israiloff.rpc4rj.service.impl.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import io.github.israiloff.rpc4rj.model.JsonRpcRequest;
import io.github.israiloff.rpc4rj.model.JsonRpcServiceInfo;
import io.github.israiloff.rpc4rj.model.RpcServiceMetaData;
import io.github.israiloff.rpc4rj.service.EndpointHandler;
import io.github.israiloff.rpc4rj.service.RJRpcProcessor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;

import static io.github.israiloff.rpc4rj.web.rest.RJRpcController.REACTIVE_REQUEST_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class EndpointHandlerImpl implements EndpointHandler<ServerWebExchange> {

    @Qualifier("requestMappingHandlerMapping")
    private final RequestMappingHandlerMapping handlerMapping;
    private final RpcServiceMetaData metaData;
    private final RJRpcProcessor processor;

    public Mono<ResponseEntity<?>> handle(@Valid @RequestBody @NotNull JsonRpcRequest request,
                                          ServerWebExchange exchange) throws Exception {
        log.debug("RPC request to common rpc endpoint. uri : {}, body : {}", exchange.getRequest().getPath().value(), request);
        exchange.getAttributes().put(REACTIVE_REQUEST_KEY, request);
        return processor.process(request, exchange.getRequest().getPath().value());
    }

    public void registerEndpoints() {
        log.trace("registerEndpoints started");
        handlerMapping.registerMapping(
                RequestMappingInfo
                        .paths(metaData.getRpcInfoList().stream().map(JsonRpcServiceInfo::getUri).toArray(String[]::new))
                        .methods(RequestMethod.POST)
                        .produces(MediaType.APPLICATION_STREAM_JSON_VALUE)
                        .build(),
                this,
                getEndpoint());
    }

    @SneakyThrows
    private Method getEndpoint() {
        log.trace("getEndpoint started");
        return this.getClass().getDeclaredMethod("handle", JsonRpcRequest.class, ServerWebExchange.class);
    }
}
