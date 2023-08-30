package io.github.israiloff.rpc4rj.annotation;

import javax.validation.constraints.NotBlank;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RJRpcMethod {
    @NotBlank
    String value();
}
