package io.github.israiloff.rpc4rj.error.exception;

public class InvalidParamsException extends Exception {

    public InvalidParamsException() {
        super("Invalid method parameter(s)");
    }
}
