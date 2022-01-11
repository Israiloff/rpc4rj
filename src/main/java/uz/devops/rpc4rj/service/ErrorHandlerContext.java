package uz.devops.rpc4rj.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uz.devops.rpc4rj.model.JsonRpcRequest;
import uz.devops.rpc4rj.model.JsonRpcResponse;
import uz.devops.rpc4rj.model.RpcServiceMetaData;
import uz.devops.rpc4rj.service.impl.error.CommonErrorHandler;
import uz.devops.rpc4rj.util.ErrorHandlerUtil;
import uz.devops.rpc4rj.util.ReflectionUtil;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class ErrorHandlerContext {

    private final List<ErrorHandler> handlers;
    private final ErrorHandler commonHandler;
    private final RpcServiceMetaData metaData;
    private final ReflectionUtil reflectionUtil;
    private final ErrorHandlerUtil handlerUtil;

    public ErrorHandlerContext(List<ErrorHandler> handlers, @Qualifier(CommonErrorHandler.BEAN_NAME) ErrorHandler commonHandler,
                               RpcServiceMetaData metaData, ReflectionUtil reflectionUtil, ErrorHandlerUtil handlerUtil) {
        this.handlerUtil = handlerUtil;
        handlers.remove(commonHandler);
        this.commonHandler = commonHandler;
        this.handlers = handlers;
        this.metaData = metaData;
        this.reflectionUtil = reflectionUtil;
    }

    public JsonRpcResponse handle(@Valid Throwable e, JsonRpcRequest request) {
        log.debug("commonHandler started", e);

        var exception = reflectionUtil.extractError(e);
        return metaData.getRpcInfoList()
                .stream()
                .filter(info -> Objects.equals(info.getMethodName(), request.getMethod()))
                .flatMap(errors -> errors.getErrors()
                        .stream()
                        .filter(error -> error.getException().equals(exception.getClass())))
                .map(errorInfo -> handlerUtil.getJsonRpcResponse(request, errorInfo, exception))
                .findFirst()
                .orElseGet(() -> handlers
                        .stream()
                        .filter(errorHandler -> errorHandler.getExceptionType().equals(exception.getClass()))
                        .map(errorHandler -> errorHandler.buildResponse(exception, request))
                        .findFirst()
                        .orElse(commonHandler.buildResponse(exception, request)));
    }
}
