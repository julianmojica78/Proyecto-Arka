package com.arka.order.domain.port.in;

import com.arka.order.domain.model.CreateOrderCommand;
import com.arka.order.domain.model.Order;

import reactor.core.publisher.Mono;

public interface CreateOrderUseCase {
    Mono<Order> create(CreateOrderCommand command);
}
