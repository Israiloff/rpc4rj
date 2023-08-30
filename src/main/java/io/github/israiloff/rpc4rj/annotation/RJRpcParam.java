package io.github.israiloff.rpc4rj.annotation;

import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RJRpcParam {
    @NotNull
    String value();
}
