package com.arka.report.infrastructure.adapter.out.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.arka.report.domain.model.ProductView;
import com.arka.report.domain.port.out.InventoryQueryPort;

import reactor.core.publisher.Flux;

@Component
public class InventoryHttpClientAdapter implements InventoryQueryPort {

    private final WebClient webClient;

    public InventoryHttpClientAdapter(
            WebClient.Builder webClientBuilder,
            @Value("${services.inventory.base-url:http://INVENTORY-SERVICE}") String inventoryBaseUrl) {
        this.webClient = webClientBuilder.baseUrl(inventoryBaseUrl).build();
    }

    @Override
    public Flux<ProductView> findAllProducts() {
        return webClient.get()
                .uri("/products")
                .retrieve()
                .bodyToFlux(ProductView.class);
    }
}
