package uz.devops.rpc4rj.service.primitive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uz.devops.rpc4rj.annotation.RpcMethod;
import uz.devops.rpc4rj.annotation.RpcParam;
import uz.devops.rpc4rj.annotation.RpcService;

import javax.validation.Valid;
import java.util.ArrayList;

@Slf4j
@Service
@Validated
@RpcService
public class JsonRpcServiceDummyImpl {

    public static final String METHOD_DUMMY_MONO = "dummyMethodMono";
    public static final String METHOD_DUMMY_FLUX = "dummyMethodFlux";

    @RpcMethod(METHOD_DUMMY_MONO)
    public Mono<DummyResponse> dummyMono(
            @Valid @RpcParam("requestOne") DummyRequestOne requestOne,
            @Valid @RpcParam("requestTwo") DummyRequestTwo requestTwo
    ) {
        log.trace("dummyMethod started");
        return Mono.just(new DummyResponse(requestOne.getData() + requestTwo.getData(), "SUCCESS"));
    }

    @RpcMethod(METHOD_DUMMY_FLUX)
    public Flux<DummyResponse> dummyFlux(
            @Valid @RpcParam("requestOne") DummyRequestOne requestOne,
            @Valid @RpcParam("requestTwo") DummyRequestTwo requestTwo
    ) {
        log.trace("dummyMethod started");
        var result = new ArrayList<DummyResponse>();
        var requestData = requestOne.getData() + requestTwo.getData();

        for (var i = 0; i < 5; i++) {
            addElement(result, requestData, i);
        }

        return Flux.fromIterable(result);
    }

    private void addElement(ArrayList<DummyResponse> result, String requestData, Integer order) {
        log.trace("addElement started for order : {}", order);
        result.add(new DummyResponse(requestData, String.valueOf(order)));
    }
}
