package io.github.israiloff.rpc4rj.service.impl.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import io.github.israiloff.rpc4rj.error.exception.MethodNotFoundException;
import io.github.israiloff.rpc4rj.model.JsonRpcRequest;
import io.github.israiloff.rpc4rj.model.RpcServiceMetaData;
import io.github.israiloff.rpc4rj.service.RJRpcProcessor;
import io.github.israiloff.rpc4rj.util.RJRpcProcessorUtil;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class RJRpcProcessorImpl implements RJRpcProcessor {

    private final RpcServiceMetaData metaData;
    private final RJRpcProcessorUtil util;

    public Mono<ResponseEntity<?>> process(@NotNull JsonRpcRequest request, String uri) throws Exception {
        log.debug("endpoint started for request : {}", request);

        util.validateRequest(request);

        var jsonRpcInfo = metaData
                .getRpcInfoList()
                .stream()
                .filter(rpcInfo -> rpcInfo.getUri().equalsIgnoreCase(uri) && util.isDesiredMethod(request, rpcInfo))
                .findFirst()
                .orElseThrow(MethodNotFoundException::new);

        var result = util.mapResult(request, jsonRpcInfo, util.executeMethod(request, jsonRpcInfo));

        log.debug("endpoint successfully ended for request : {}", request);
        return result;
    }
}
