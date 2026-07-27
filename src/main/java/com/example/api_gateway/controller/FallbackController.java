package com.example.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/products")
    public Mono<ResponseEntity<Map<String, String>>> productFallback() {

        Map<String, String> response = Map.of(
                "message", "Product Service is temporarily unavailable. Please try again later."
        );

        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(response)
        );
    }
}