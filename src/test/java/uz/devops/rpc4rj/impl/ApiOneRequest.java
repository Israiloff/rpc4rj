package uz.devops.rpc4rj.impl;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class ApiOneRequest {

    @NotNull
    DummyRequestOne requestOne;

    @NotNull
    DummyRequestTwo requestTwo;
}
