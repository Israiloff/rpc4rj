package uz.devops.rpc4rj.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uz.devops.rpc4rj.annotation.RJRpcMethod;
import uz.devops.rpc4rj.annotation.RJRpcParam;
import uz.devops.rpc4rj.annotation.RJRpcService;

import javax.validation.Valid;
import java.util.ArrayList;

@Slf4j
@Service
@Validated
@RJRpcService(JsonRpcDummyApiTwo.URI)
public class JsonRpcDummyApiTwo {

    public static final String METHOD_DUMMY_MONO = "dummyMethodMono";
    public static final String METHOD_DUMMY_FLUX = "dummyMethodFlux";
    public static final String URI = "/api/rpc/dummy/two";

    @RJRpcMethod(METHOD_DUMMY_MONO)
    public Mono<DummyResponse> dummyMono(@Valid @RJRpcParam("requestOne") DummyRequestOne requestOne) {
        log.trace("dummyMethod started");
        return Mono.just(new DummyResponse(requestOne.getData(), "SUCCESS"));
    }

    @RJRpcMethod(METHOD_DUMMY_FLUX)
    public Flux<DummyResponse> dummyFlux(@Valid @RJRpcParam("requestOne") DummyRequestOne requestOne) {
        log.trace("dummyMethod started");
        var result = new ArrayList<DummyResponse>();
        var requestData = requestOne.getData();

        for (var i = 0; i < 2; i++) {
            addElement(result, requestData, i);
        }

        return Flux.fromIterable(result);
    }

    private void addElement(ArrayList<DummyResponse> result, String requestData, Integer order) {
        log.trace("addElement started for order : {}", order);
        result.add(new DummyResponse(requestData, String.valueOf(order)));
    }
}
