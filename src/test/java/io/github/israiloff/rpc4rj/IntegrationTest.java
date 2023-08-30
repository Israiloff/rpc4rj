package io.github.israiloff.rpc4rj;

import io.github.israiloff.rpc4rj.config.Rpc4rjApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = Rpc4rjApplication.class)
public @interface IntegrationTest {
}
