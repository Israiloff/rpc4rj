package io.github.israiloff.rpc4rj.service;

import io.github.israiloff.rpc4rj.model.JsonRpcRequest;
import io.github.israiloff.rpc4rj.model.JsonRpcResponse;

public interface ErrorHandler {
    Class<?> getExceptionType();

    JsonRpcResponse buildResponse(Throwable e, JsonRpcRequest request);
}
