package uz.devops.rpc4rj.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uz.devops.rpc4rj.error.handler.impl.CommonErrorHandler;
import uz.devops.rpc4rj.model.JsonRpcRequest;
import uz.devops.rpc4rj.model.JsonRpcResponse;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Component
public class ErrorHandlerContext {

    private final List<ErrorHandler> handlers;
    private final ErrorHandler commonHandler;

    public ErrorHandlerContext(List<ErrorHandler> handlers, @Qualifier(CommonErrorHandler.BEAN_NAME) ErrorHandler commonHandler) {
        handlers.remove(commonHandler);
        this.handlers = handlers;
        this.commonHandler = commonHandler;
    }

    public JsonRpcResponse handle(@Valid Throwable e, JsonRpcRequest request) {
        log.debug("commonHandler started for exception : {}", e.getClass().getSimpleName());

        return handlers
                .stream()
                .filter(errorHandler -> errorHandler.getExceptionType().equals(e.getClass()))
                .map(errorHandler -> errorHandler.buildResponse(e, request))
                .findFirst()
                .orElse(commonHandler.buildResponse(e, request));
    }
}
