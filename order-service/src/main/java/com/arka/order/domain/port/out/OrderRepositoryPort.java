package com.arka.order.domain.port.out;

import java.time.Instant;

import com.arka.order.domain.model.Order;
import com.arka.order.domain.model.OrderStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepositoryPort {
    Mono<Order> save(Order order);
    Mono<Order> findById(Long id);
    Mono<Order> update(Order order);
    Flux<Order> findByStatusAndCreatedAtBetween(OrderStatus status, Instant start, Instant end);
}
