package io.github.israiloff.rpc4rj.service;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import io.github.israiloff.rpc4rj.model.JsonRpcRequest;

import javax.validation.constraints.NotNull;

public interface RJRpcProcessor {
    Mono<ResponseEntity<?>> process(@NotNull JsonRpcRequest request, String uri) throws Exception;
}
