package uz.devops.rpc4rj.model;

import lombok.Value;

@Value
public class JsonRpcErrorInfo {
    Class<? extends Throwable> exception;
    int code;
    String message;
    String data;
}
