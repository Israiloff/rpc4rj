package uz.devops.rpc4rj.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;
import uz.devops.rpc4rj.model.JsonRpcRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface EndpointHandler<TRequestDetails> {
    void registerEndpoints();

    Mono<ResponseEntity<?>> handle(@Valid @RequestBody @NotNull JsonRpcRequest request,
                                   TRequestDetails details) throws Exception;
}
