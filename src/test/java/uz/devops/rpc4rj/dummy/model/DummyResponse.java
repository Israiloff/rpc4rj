package uz.devops.rpc4rj.dummy.model;

import lombok.Value;

@Value
public class DummyResponse {

    String requestData;
    String result;
}
