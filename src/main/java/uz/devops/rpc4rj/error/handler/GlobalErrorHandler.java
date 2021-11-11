package uz.devops.rpc4rj.error.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import uz.devops.rpc4rj.config.RpcConfiguration;
import uz.devops.rpc4rj.controller.JsonRpcController;
import uz.devops.rpc4rj.model.JsonRpcRequest;

@Slf4j
@Order(-3)
@Configuration
@RequiredArgsConstructor
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Qualifier(RpcConfiguration.OBJECT_MAPPER_BEAN_NAME)
    private final ObjectMapper objectMapper;
    private final ErrorHandlerContext errorHandlerContext;

    @SneakyThrows
    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        log.error("error occurred while processing request", throwable);
        log.debug("error handle started for exception : {}", throwable.getClass().getSimpleName());
        serverWebExchange.getResponse().setStatusCode(HttpStatus.OK);
        serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_STREAM_JSON);
        var request = (JsonRpcRequest) serverWebExchange.getAttributes().get(JsonRpcController.REACTIVE_REQUEST_KEY);
        return serverWebExchange.getResponse().writeWith(getBodyBuffer(serverWebExchange, throwable, request));
    }

    private Mono<DataBuffer> getBodyBuffer(ServerWebExchange serverWebExchange, Throwable throwable, JsonRpcRequest request) {
        log.trace("getBodyBuffer started");
        return Mono.just(serverWebExchange.getResponse().bufferFactory().wrap(getContentBytes(throwable, request)));
    }

    @SneakyThrows
    private byte[] getContentBytes(Throwable throwable, JsonRpcRequest request) {
        log.trace("getContentBytes started for request with id : {}", request.getId());
        return objectMapper.writeValueAsBytes(errorHandlerContext.handle(throwable, request));
    }
}
