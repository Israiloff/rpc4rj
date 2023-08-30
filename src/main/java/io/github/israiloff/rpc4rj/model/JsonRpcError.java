package io.github.israiloff.rpc4rj.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonRpcError {

    @NotNull
    @JsonProperty("code")
    Integer code;

    @NotNull
    @JsonProperty("message")
    String message;

    @JsonProperty("data")
    String data;
}
