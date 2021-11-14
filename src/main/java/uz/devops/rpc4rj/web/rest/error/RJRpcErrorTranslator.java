package uz.devops.rpc4rj.web.rest.error;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import uz.devops.rpc4rj.model.JsonRpcRequest;
import uz.devops.rpc4rj.model.JsonRpcResponse;
import uz.devops.rpc4rj.service.EndpointHandler;
import uz.devops.rpc4rj.service.ErrorHandlerContext;
import uz.devops.rpc4rj.util.ErrorHandlerUtil;
import uz.devops.rpc4rj.web.rest.RJRpcController;

@Slf4j
@Component
@Order(-3)
@RestControllerAdvice(assignableTypes = {RJRpcController.class, ErrorHandlerContext.class, EndpointHandler.class})
@RequiredArgsConstructor
public class RJRpcErrorTranslator {

    private final ErrorHandlerContext errorHandlerContext;
    private final ErrorHandlerUtil util;

    @SneakyThrows
    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<JsonRpcResponse>> handle(Throwable e, ServerWebExchange serverWebExchange) {
        log.error("error occurred while processing request", e);
        log.debug("error handle started for exception : {}", e.getClass().getSimpleName());
        var request = (JsonRpcRequest) serverWebExchange.getAttributes().get(RJRpcController.REACTIVE_REQUEST_KEY);
        return util.wrapResponse(errorHandlerContext.handle(e, request));
    }
}
