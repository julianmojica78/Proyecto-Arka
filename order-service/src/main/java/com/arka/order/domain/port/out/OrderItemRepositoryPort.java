package com.arka.order.domain.port.out;

import java.util.List;

import com.arka.order.domain.model.OrderItem;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderItemRepositoryPort {

    Mono<Void> saveAll(List<OrderItem> items, Long orderId);

    Flux<OrderItem> findByOrderId(Long orderId);
}
