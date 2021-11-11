package uz.devops.rpc4rj.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.devops.rpc4rj.util.ResolverUtil;

import java.util.List;

@Slf4j
@Component
public class RpcServiceMetaData {

    private final List<JsonRpcServiceInfo> rpcInfoList;
    private final ResolverUtil util;

    public RpcServiceMetaData(ResolverUtil util) {
        log.trace("");
        this.util = util;
        this.rpcInfoList = this.util.getRpcInfoList();
    }

    public List<JsonRpcServiceInfo> getRpcInfoList() {
        return rpcInfoList;
    }
}
