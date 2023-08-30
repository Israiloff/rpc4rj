package io.github.israiloff.rpc4rj.annotation;

import javax.validation.constraints.NotBlank;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RJRpcService {
    @NotBlank
    String value();
}
