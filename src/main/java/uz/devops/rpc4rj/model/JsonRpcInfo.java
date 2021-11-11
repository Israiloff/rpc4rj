package uz.devops.rpc4rj.model;

import lombok.Value;

import java.lang.reflect.Method;
import java.util.List;

@Value
public class JsonRpcInfo {

    Object instance;
    String methodName;
    Method method;
    List<JsonRpcParamInfo> params;
}
