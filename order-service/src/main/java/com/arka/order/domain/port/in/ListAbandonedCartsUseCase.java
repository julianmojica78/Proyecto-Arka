package com.arka.order.domain.port.in;

import com.arka.order.domain.model.Cart;

import reactor.core.publisher.Flux;

public interface ListAbandonedCartsUseCase {

    Flux<Cart> list();
}
