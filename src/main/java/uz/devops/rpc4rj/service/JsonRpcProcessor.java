package uz.devops.rpc4rj.service;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import uz.devops.rpc4rj.model.JsonRpcRequest;

import javax.validation.constraints.NotNull;

public interface JsonRpcProcessor {
    Mono<ResponseEntity<?>> process(@NotNull JsonRpcRequest request) throws Exception;
}
