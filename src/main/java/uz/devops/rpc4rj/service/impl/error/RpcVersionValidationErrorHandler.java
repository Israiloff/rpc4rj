package uz.devops.rpc4rj.service.impl.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.devops.rpc4rj.error.exception.JsonRpcVersionValidationException;
import uz.devops.rpc4rj.model.JsonRpcRequest;
import uz.devops.rpc4rj.model.JsonRpcResponse;
import uz.devops.rpc4rj.service.ErrorHandler;
import uz.devops.rpc4rj.util.ErrorHandlerUtil;

@Slf4j
@Component
@RequiredArgsConstructor
public class RpcVersionValidationErrorHandler implements ErrorHandler {

    private final ErrorHandlerUtil util;

    @Override
    public Class<?> getExceptionType() {
        return JsonRpcVersionValidationException.class;
    }

    @Override
    public JsonRpcResponse buildResponse(Throwable e, JsonRpcRequest request) {
        log.trace("buildResponse started");
        return util.buildResponse(request, -32600, e, getMessage(e));
    }

    private String getMessage(Throwable e) {
        return "The JSON sent is not a valid Request object: " + e.getMessage();
    }
}
