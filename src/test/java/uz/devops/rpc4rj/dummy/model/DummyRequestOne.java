package uz.devops.rpc4rj.dummy.model;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class DummyRequestOne {

    @NotNull(message = "field must not be null")
    String data;
}
