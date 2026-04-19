package com.arka.inventory.domain.port.in;

import com.arka.inventory.domain.model.Product;

import reactor.core.publisher.Flux;

public interface GetProductsUseCase {
    Flux<Product> getAll();
}