package uz.devops.rpc4rj.error.base;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public interface JsonRpcExceptionDetails {
    @Min(-32098)
    @Max(-32000)
    Integer getCode();

    String getMessage();
}
