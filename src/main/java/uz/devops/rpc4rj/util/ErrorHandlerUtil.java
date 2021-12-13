package uz.devops.rpc4rj.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import uz.devops.rpc4rj.model.JsonRpcError;
import uz.devops.rpc4rj.model.JsonRpcErrorInfo;
import uz.devops.rpc4rj.model.JsonRpcRequest;
import uz.devops.rpc4rj.model.JsonRpcResponse;

@Slf4j
@Component
@AllArgsConstructor
public class ErrorHandlerUtil {

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

    public JsonRpcResponse getJsonRpcResponse(JsonRpcRequest request, JsonRpcErrorInfo errorInfo, Throwable exception) {
        return new JsonRpcResponse(request.getId(), request.getJsonrpc(), null, getError(errorInfo, exception));
    }

    private JsonRpcError getError(JsonRpcErrorInfo errorInfo, Throwable exception) {
        return new JsonRpcError(errorInfo.getCode(), getString(errorInfo.getMessage(), exception.getMessage()), getString(errorInfo.getData(), exception.getClass().getName()));
    }

    private String getString(String value, String fallback) {
        return value == null || value.equals("") ? fallback : value;
    }
}
