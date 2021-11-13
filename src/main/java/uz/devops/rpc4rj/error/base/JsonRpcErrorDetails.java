package uz.devops.rpc4rj.error.base;

import javax.validation.constraints.NotNull;

public interface JsonRpcErrorDetails {
    @NotNull
    Integer getCode();

    String getMessage();
}
