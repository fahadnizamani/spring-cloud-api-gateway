package com.example.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String correlationId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Correlation-Id");

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header("X-Correlation-Id", correlationId)
                .build();

        log.info("REQ {} {} correlationId={}",
                request.getMethod(),
                request.getURI().getPath(),
                correlationId);

        exchange.getResponse().getHeaders()
                .set("X-Correlation-Id", correlationId);

        long start = System.currentTimeMillis();
        final String cid = correlationId;

        return chain.filter(exchange.mutate().request(request).build())
                .then(Mono.fromRunnable(() -> {
                    long duration = System.currentTimeMillis() - start;

                    log.info("RES {} {} status={} durationMs={} correlationId={}",
                            request.getMethod(),
                            request.getURI().getPath(),
                            exchange.getResponse().getStatusCode(),
                            duration,
                            cid);
                }));

        //return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return -1;
    }


}
