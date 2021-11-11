package uz.devops.rpc4rj.error.handler;

import uz.devops.rpc4rj.model.JsonRpcRequest;
import uz.devops.rpc4rj.model.JsonRpcResponse;

public interface ErrorHandler {
    Class<?> getExceptionType();

    JsonRpcResponse buildResponse(Throwable e, JsonRpcRequest request);
}
