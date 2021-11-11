package uz.devops.rpc4rj.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import uz.devops.rpc4rj.model.JsonRpcRequest;
import uz.devops.rpc4rj.service.JsonRpcProcessor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/rpc")
public class JsonRpcController {

    public static final String REACTIVE_REQUEST_KEY = "reactiveRequest";
    private final JsonRpcProcessor service;

    @GetMapping("/echo")
    public Mono<ResponseEntity<String>> echo() {
        log.trace("REST request to reactive echo");
        return Mono.just(ResponseEntity.ok("SUCCESS"));
    }

    @PostMapping(value = "/reactive", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Mono<ResponseEntity<?>> reactiveEndpoint(@Valid @RequestBody @NotNull JsonRpcRequest request, ServerWebExchange exchange)
            throws Exception {
        log.debug("RPC request to common rpc endpoint : {}", request);
        exchange.getAttributes().put(REACTIVE_REQUEST_KEY, request);
        return service.process(request);
    }
}
