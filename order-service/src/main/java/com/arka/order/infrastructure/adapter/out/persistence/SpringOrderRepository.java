package com.arka.order.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.arka.order.domain.model.OrderStatus;

import reactor.core.publisher.Flux;

public interface SpringOrderRepository extends ReactiveCrudRepository<OrderEntity, Long> {

    Flux<OrderEntity> findByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime start, LocalDateTime end);
}
