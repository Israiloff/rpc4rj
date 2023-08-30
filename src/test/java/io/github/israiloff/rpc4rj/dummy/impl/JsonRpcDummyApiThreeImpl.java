package io.github.israiloff.rpc4rj.dummy.impl;

import io.github.israiloff.rpc4rj.dummy.model.DummyRequestOne;
import io.github.israiloff.rpc4rj.dummy.model.DummyRequestTwo;
import io.github.israiloff.rpc4rj.dummy.model.DummyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import io.github.israiloff.rpc4rj.dummy.JsonRpcDummyApiThree;

@Slf4j
@Service
public class JsonRpcDummyApiThreeImpl implements JsonRpcDummyApiThree {

    public Mono<DummyResponse> dummyMono(DummyRequestOne requestOne, DummyRequestTwo requestTwo) {
        log.trace("dummyMethod started");
        return Mono.just(new DummyResponse(requestOne.getData() + requestTwo.getData(), "SUCCESS"));
    }
}
