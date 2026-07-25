package com.example.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            if (exchange.getRequest().getRemoteAddress() == null) {
                return Mono.just("unknown");
            }

            return Mono.just(
                    exchange.getRequest()
                            .getRemoteAddress()
                            .getAddress()
                            .getHostAddress()
            );
        };
    }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String email = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-User-Email");

            if (email == null || email.isBlank()) {
                return Mono.just("anonymous");
            }

            return Mono.just(email);
        };
    }
}
