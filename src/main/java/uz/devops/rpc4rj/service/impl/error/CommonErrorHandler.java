package uz.devops.rpc4rj.service.impl.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.devops.rpc4rj.model.JsonRpcRequest;
import uz.devops.rpc4rj.model.JsonRpcResponse;
import uz.devops.rpc4rj.service.ErrorHandler;
import uz.devops.rpc4rj.util.ErrorHandlerUtil;

@Slf4j
@Component(CommonErrorHandler.BEAN_NAME)
@RequiredArgsConstructor
public class CommonErrorHandler implements ErrorHandler {

    public static final String BEAN_NAME = "commonErrorHandler";

    private final ErrorHandlerUtil util;

    @Override
    public Class<?> getExceptionType() {
        return Throwable.class;
    }

    @Override
    public JsonRpcResponse buildResponse(Throwable e, JsonRpcRequest request) {
        log.trace("buildResponse started");
        return util.buildResponse(request, -32099, e);
    }
}
