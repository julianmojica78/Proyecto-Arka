package com.arka.order.domain.port.out;

import com.arka.order.domain.model.Cart;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CartRepositoryPort {

    Mono<Cart> save(Cart cart);

    Mono<Cart> findById(Long id);

    Flux<Cart> findAbandoned();
}
