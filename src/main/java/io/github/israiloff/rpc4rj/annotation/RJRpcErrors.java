package io.github.israiloff.rpc4rj.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RJRpcErrors {
    RJRpcError[] value();
}
