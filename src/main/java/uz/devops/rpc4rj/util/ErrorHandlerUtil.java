package uz.devops.rpc4rj.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import uz.devops.rpc4rj.error.base.JsonRpcExceptionDetails;
import uz.devops.rpc4rj.model.JsonRpcError;
import uz.devops.rpc4rj.model.JsonRpcRequest;
import uz.devops.rpc4rj.model.JsonRpcResponse;

import java.util.Arrays;

@Slf4j
@Component
@AllArgsConstructor
public class ErrorHandlerUtil {

    public boolean isDetailedError(Throwable e) {
        log.trace("isDetailedError started");
        return Arrays.asList(e.getClass().getInterfaces()).contains(JsonRpcExceptionDetails.class);
    }

    public JsonRpcResponse buildResponse(JsonRpcRequest request, Integer code, Throwable e) {
        log.trace("buildResponse started");
        return new JsonRpcResponse(request.getId(), request.getJsonrpc(), null, getError(e, code));
    }

    public JsonRpcResponse buildResponse(JsonRpcRequest request, Integer code, Throwable e, String message) {
        log.trace("buildResponse started");
        return new JsonRpcResponse(request.getId(), request.getJsonrpc(), null, getError(e, code, message));
    }

    public Mono<ResponseEntity<JsonRpcResponse>> wrapResponse(JsonRpcResponse response) {
        log.trace("wrapResponse started");
        return Mono.just(ResponseEntity.ok(response));
    }

    private JsonRpcError getError(Throwable e, Integer code) {
        log.trace("getError started");
        return new JsonRpcError(code, e.getMessage(), e.getClass().getSimpleName());
    }

    private JsonRpcError getError(Throwable e, Integer code, String message) {
        log.trace("getError started");
        return new JsonRpcError(code, message, e.getClass().getSimpleName());
    }
}
