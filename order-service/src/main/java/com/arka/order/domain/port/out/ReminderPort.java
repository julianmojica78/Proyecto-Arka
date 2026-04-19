package com.arka.order.domain.port.out;

import com.arka.order.domain.model.Cart;

import reactor.core.publisher.Mono;

public interface ReminderPort {

    Mono<Void> sendCartReminder(Cart cart);
}
