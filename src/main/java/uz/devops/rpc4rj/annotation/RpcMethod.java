package uz.devops.rpc4rj.annotation;

import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RpcMethod {
    @NotNull
    String value();
}
