package io.github.israiloff.rpc4rj.error.exception;

public class MethodNotFoundException extends Exception {

    public MethodNotFoundException() {
        super("The method does not exist / is not available");
    }
}
