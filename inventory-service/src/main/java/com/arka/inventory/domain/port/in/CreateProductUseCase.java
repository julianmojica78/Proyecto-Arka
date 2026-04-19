package com.arka.inventory.domain.port.in;

import com.arka.inventory.domain.model.Product;

import reactor.core.publisher.Mono;

public interface CreateProductUseCase {
    Mono<Product> create(Product product);
}