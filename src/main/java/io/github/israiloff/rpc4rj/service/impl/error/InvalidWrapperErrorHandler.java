package io.github.israiloff.rpc4rj.service.impl.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.github.israiloff.rpc4rj.error.exception.InvalidWrapperException;
import io.github.israiloff.rpc4rj.model.JsonRpcRequest;
import io.github.israiloff.rpc4rj.model.JsonRpcResponse;
import io.github.israiloff.rpc4rj.service.ErrorHandler;
import io.github.israiloff.rpc4rj.util.ErrorHandlerUtil;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvalidWrapperErrorHandler implements ErrorHandler {

    private final ErrorHandlerUtil util;

    @Override
    public Class<?> getExceptionType() {
        return InvalidWrapperException.class;
    }

    @Override
    public JsonRpcResponse buildResponse(Throwable e, JsonRpcRequest request) {
        log.trace("buildResponse started");
        return util.buildResponse(request, -32603, e, "Invalid method parameter(s)");
    }
}
