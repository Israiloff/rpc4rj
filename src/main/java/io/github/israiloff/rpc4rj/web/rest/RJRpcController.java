package io.github.israiloff.rpc4rj.web.rest;

import io.github.israiloff.rpc4rj.service.EndpointHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.PostConstruct;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RJRpcController {

    public static final String REACTIVE_REQUEST_KEY = "reactiveRequest";

    private final EndpointHandler<ServerWebExchange> endpointHandler;

    @PostConstruct
    public void init() {
        log.trace("init started");
        endpointHandler.registerEndpoints();
    }
}
