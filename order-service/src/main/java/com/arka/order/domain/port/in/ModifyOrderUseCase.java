package com.arka.order.domain.port.in;

import com.arka.order.domain.model.ModifyOrderCommand;
import com.arka.order.domain.model.Order;

import reactor.core.publisher.Mono;

public interface ModifyOrderUseCase {

    Mono<Order> modify(ModifyOrderCommand command);
}
