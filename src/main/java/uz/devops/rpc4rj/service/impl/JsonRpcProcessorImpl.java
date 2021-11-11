package uz.devops.rpc4rj.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uz.devops.rpc4rj.error.exception.MethodNotFoundException;
import uz.devops.rpc4rj.model.JsonRpcInfo;
import uz.devops.rpc4rj.model.JsonRpcRequest;
import uz.devops.rpc4rj.service.JsonRpcProcessor;
import uz.devops.rpc4rj.util.ResolverUtil;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Service
public class JsonRpcProcessorImpl implements JsonRpcProcessor {

    private final List<JsonRpcInfo> rpcInfoList;
    private final ResolverUtil util;

    public JsonRpcProcessorImpl(ResolverUtil util) {
        this.util = util;
        rpcInfoList = util.getRpcInfoList();
    }

    public Mono<ResponseEntity<?>> process(@NotNull JsonRpcRequest request) throws Exception {
        log.debug("endpoint started for request : {}", request);

        var jsonRpcInfo = rpcInfoList
                .stream()
                .filter(rpcInfo -> util.isDesiredMethod(request, rpcInfo))
                .findFirst()
                .orElseThrow(MethodNotFoundException::new);

        var result = util.mapResult(request, jsonRpcInfo, util.executeMethod(request, jsonRpcInfo));

        log.debug("endpoint successfully ended for request : {}", request);
        return result;
    }
}
