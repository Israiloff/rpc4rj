package uz.devops.rpc4rj.error.exception;

public class InvalidWrapperException extends Exception {

    public InvalidWrapperException() {
        super("Result wrapper must be reactive type (Mono/Flux)");
    }
}
