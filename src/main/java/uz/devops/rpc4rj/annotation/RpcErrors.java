package uz.devops.rpc4rj.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RpcErrors {
    RpcError[] value();
}
