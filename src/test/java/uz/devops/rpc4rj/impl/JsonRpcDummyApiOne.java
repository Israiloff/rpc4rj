package uz.devops.rpc4rj.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uz.devops.rpc4rj.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

@Slf4j
@Service
@Validated
@RpcService(JsonRpcDummyApiOne.URI)
public class JsonRpcDummyApiOne {

    public static final String METHOD_DUMMY_MONO = "dummyMethodMono";
    public static final String METHOD_DUMMY_MONO_CUSTOM_ERROR = "dummyMethodMonoCustomError";
    public static final String METHOD_DUMMY_FLUX = "dummyMethodFlux";
    public static final String URI = "/api/rpc/dummy/one";
    public static final int DUMMY_ERROR_CODE = 998;
    public static final int DUMMY_ERROR_CODE2 = 999;
    public static final String DUMMY_MESSAGE = "DUMMY_MESSAGE";
    public static final String DUMMY_MESSAGE2 = "DUMMY_MESSAGE2";
    public static final String DUMMY_DATA = "DUMMY_DATA";
    public static final String DUMMY_DATA2 = "DUMMY_DATA2";

    @RpcMethod(METHOD_DUMMY_MONO)
    public Mono<DummyResponse> dummyMono(
            @Valid @RpcParam("requestOne") DummyRequestOne requestOne,
            @Valid @RpcParam("requestTwo") DummyRequestTwo requestTwo
    ) {
        log.trace("dummyMethod started");
        return Mono.just(new DummyResponse(requestOne.getData() + requestTwo.getData(), "SUCCESS"));
    }

    @RpcMethod(METHOD_DUMMY_MONO_CUSTOM_ERROR)
    @RpcErrors({
            @RpcError(exception = DummyActiveException.class, code = DUMMY_ERROR_CODE, message = DUMMY_MESSAGE, data = DUMMY_DATA),
            @RpcError(exception = DummyInactiveException.class, code = DUMMY_ERROR_CODE2, message = DUMMY_MESSAGE2, data = DUMMY_DATA2)
    })
    public Mono<DummyResponse> dummyMonoCustomError(
            @Valid @RpcParam("requestOne") DummyRequestOne requestOne,
            @Valid @RpcParam("requestTwo") DummyRequestTwo requestTwo
    ) throws DummyActiveException {
        log.trace("dummyMonoCustomError started");
        throw new DummyActiveException();
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
