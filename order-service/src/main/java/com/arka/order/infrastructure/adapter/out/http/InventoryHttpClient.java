package com.arka.order.infrastructure.adapter.out.http;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;

@Component
public class InventoryHttpClient {

    private static final ParameterizedTypeReference<Map<String, Object>> PRODUCT_RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final WebClient webClient;

    public InventoryHttpClient(
            @Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClientBuilder,
            @Value("${services.inventory.base-url:http://inventory-service}") String inventoryBaseUrl) {
        this.webClient = webClientBuilder.baseUrl(inventoryBaseUrl).build();
    }

    public Flux<Map<String, Object>> findAllProducts() {
        return webClient.get()
                .uri("/products")
                .retrieve()
                .bodyToFlux(PRODUCT_RESPONSE_TYPE);
    }
}
