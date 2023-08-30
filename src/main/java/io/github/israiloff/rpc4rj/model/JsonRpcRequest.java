package io.github.israiloff.rpc4rj.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Value
public class JsonRpcRequest implements Serializable {

    @NotNull
    @JsonProperty("id")
    Long id;

    @NotBlank
    @JsonProperty("jsonrpc")
    String jsonrpc;

    @NotBlank
    @JsonProperty("method")
    String method;

    @NotNull
    @JsonProperty("params")
    Object params;
}
