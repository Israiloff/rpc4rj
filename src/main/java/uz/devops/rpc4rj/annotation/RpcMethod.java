package uz.devops.rpc4rj.annotation;

import javax.validation.constraints.NotBlank;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RpcMethod {
    @NotBlank
    String value();
}
