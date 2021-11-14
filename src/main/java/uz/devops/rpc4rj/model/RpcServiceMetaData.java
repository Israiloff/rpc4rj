package uz.devops.rpc4rj.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.devops.rpc4rj.util.RJRpcProcessorUtil;

import java.util.List;

@Slf4j
@Component
public class RpcServiceMetaData {

    private final List<JsonRpcServiceInfo> rpcInfoList;

    public RpcServiceMetaData(RJRpcProcessorUtil util) {
        this.rpcInfoList = util.getRpcInfoList();
    }

    public List<JsonRpcServiceInfo> getRpcInfoList() {
        log.trace("RpcServiceMetaData started");
        return rpcInfoList;
    }
}
