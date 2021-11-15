package uz.devops.rpc4rj.dummy;

import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import uz.devops.rpc4rj.annotation.RJRpcMethod;
import uz.devops.rpc4rj.annotation.RJRpcParam;
import uz.devops.rpc4rj.annotation.RJRpcService;
import uz.devops.rpc4rj.dummy.model.DummyRequestOne;
import uz.devops.rpc4rj.dummy.model.DummyRequestTwo;
import uz.devops.rpc4rj.dummy.model.DummyResponse;

import javax.validation.Valid;

@Validated
@RJRpcService("/api/rpc/dummy/three")
public interface JsonRpcDummyApiThree {
    @RJRpcMethod("dummyMethodMono")
    Mono<DummyResponse> dummyMono(@Valid @RJRpcParam("requestOne") DummyRequestOne requestOne, @Valid @RJRpcParam("requestTwo") DummyRequestTwo requestTwo);
}
