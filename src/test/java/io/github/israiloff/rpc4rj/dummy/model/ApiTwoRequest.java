package io.github.israiloff.rpc4rj.dummy.model;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class ApiTwoRequest {

    @NotNull
    DummyRequestOne requestOne;
}
