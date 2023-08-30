package io.github.israiloff.rpc4rj.error.exception;

public class JsonRpcVersionValidationException extends Exception {
    public JsonRpcVersionValidationException(String version) {
        super("JSON-RPC version incorrect. Supported version: " + version);
    }
}
