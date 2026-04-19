package com.arka.order.infrastructure.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpringOrderItemRepository extends ReactiveCrudRepository<OrderItemEntity, Long> {

	Flux<OrderItemEntity> findByOrderId(Long orderId);

	Mono<Void> deleteByOrderId(Long orderId);
}