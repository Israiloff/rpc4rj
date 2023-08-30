package io.github.israiloff.rpc4rj.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@Slf4j
@Component
public class ReflectionUtil {

    public Throwable extractError(Throwable e) {
        log.trace("extractError started");
        return Objects.equals(e.getClass(), InvocationTargetException.class)
                ? getTargetException(e)
                : e;
    }

    private Throwable getTargetException(Throwable e) {
        log.trace("getTargetException started");
        var ex = ((InvocationTargetException) e).getTargetException();
        return ex != null ? ex : e;
    }
}
