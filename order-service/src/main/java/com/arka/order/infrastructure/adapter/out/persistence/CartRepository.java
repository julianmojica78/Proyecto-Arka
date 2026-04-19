package com.arka.order.infrastructure.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.arka.order.domain.model.CartStatus;

import reactor.core.publisher.Flux;

public interface CartRepository extends ReactiveCrudRepository<CartEntity, Long> {

    Flux<CartEntity> findByStatusOrderByUpdatedAtAsc(CartStatus status);
}
