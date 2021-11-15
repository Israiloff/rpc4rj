package uz.devops.rpc4rj.dummy.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uz.devops.rpc4rj.dummy.JsonRpcDummyApiThree;
import uz.devops.rpc4rj.dummy.model.DummyRequestOne;
import uz.devops.rpc4rj.dummy.model.DummyRequestTwo;
import uz.devops.rpc4rj.dummy.model.DummyResponse;

@Slf4j
@Service
public class JsonRpcDummyApiThreeImpl implements JsonRpcDummyApiThree {

    public Mono<DummyResponse> dummyMono(DummyRequestOne requestOne, DummyRequestTwo requestTwo) {
        log.trace("dummyMethod started");
        return Mono.just(new DummyResponse(requestOne.getData() + requestTwo.getData(), "SUCCESS"));
    }
}
