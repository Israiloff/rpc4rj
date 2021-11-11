package uz.devops.rpc4rj.impl;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class ApiTwoRequest {

    @NotNull
    DummyRequestOne requestOne;
}
