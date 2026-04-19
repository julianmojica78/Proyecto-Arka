package com.arka.order.domain.port.in;

import com.arka.order.domain.model.Cart;

import reactor.core.publisher.Mono;

public interface SaveCartUseCase {

    Mono<Cart> save(Cart cart);
}
