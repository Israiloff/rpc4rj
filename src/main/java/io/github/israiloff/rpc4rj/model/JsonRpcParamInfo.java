package io.github.israiloff.rpc4rj.model;

import lombok.Value;

@Value
public class JsonRpcParamInfo {

    String name;
    Class<?> type;
    String order;
}
