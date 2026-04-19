package com.arka.inventory.infrastructure.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, Long> {

    Mono<ProductEntity> findById(Long id);

    Flux<ProductEntity> findByStockLessThan(Integer threshold);
}
