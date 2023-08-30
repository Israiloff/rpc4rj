package io.github.israiloff.rpc4rj.dummy;

import io.github.israiloff.rpc4rj.dummy.model.DummyRequestOne;
import io.github.israiloff.rpc4rj.dummy.model.DummyRequestTwo;
import io.github.israiloff.rpc4rj.dummy.model.DummyResponse;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import io.github.israiloff.rpc4rj.annotation.RJRpcMethod;
import io.github.israiloff.rpc4rj.annotation.RJRpcParam;
import io.github.israiloff.rpc4rj.annotation.RJRpcService;

import javax.validation.Valid;

@Validated
@RJRpcService("/api/rpc/dummy/three")
public interface JsonRpcDummyApiThree {
    @RJRpcMethod("dummyMethodMono")
    Mono<DummyResponse> dummyMono(@Valid @RJRpcParam("requestOne") DummyRequestOne requestOne, @Valid @RJRpcParam("requestTwo") DummyRequestTwo requestTwo);
}
