package com.arka.order.infrastructure.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CartItemRepository extends ReactiveCrudRepository<CartItemEntity, Long> {

    Flux<CartItemEntity> findByCartId(Long cartId);

    Mono<Void> deleteByCartId(Long cartId);
}
