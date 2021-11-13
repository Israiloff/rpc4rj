package uz.devops.rpc4rj.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface RpcError {
    Class<? extends Throwable> exception();

    int code();

    String message() default "";

    String data() default "";
}
