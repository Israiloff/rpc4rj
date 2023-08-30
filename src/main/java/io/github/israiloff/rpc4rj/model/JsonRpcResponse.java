package io.github.israiloff.rpc4rj.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonRpcResponse implements Serializable {

    @NotNull
    @JsonProperty("id")
    Long id;

    @NotBlank
    @JsonProperty("jsonrpc")
    String jsonrpc;

    @JsonProperty("result")
    Object result;

    @JsonProperty("error")
    JsonRpcError error;
}
