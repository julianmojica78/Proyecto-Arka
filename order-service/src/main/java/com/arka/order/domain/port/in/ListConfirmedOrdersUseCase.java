package com.arka.order.domain.port.in;

import java.time.Instant;

import com.arka.order.domain.model.Order;

import reactor.core.publisher.Flux;

public interface ListConfirmedOrdersUseCase {

    Flux<Order> list(Instant start, Instant end);
}
