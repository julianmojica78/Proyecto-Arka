package com.arka.inventory.domain.port.out;

import com.arka.inventory.domain.model.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepositoryPort {

    Mono<Product> save(Product product);

    Flux<Product> findAll();

    Mono<Product> findById(Long id);

    Flux<Product> findByStockLessThan(Integer threshold);
}
