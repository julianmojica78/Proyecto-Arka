package com.arka.order.domain.port.out;

import reactor.core.publisher.Mono;

public interface TransactionPort {

    <T> Mono<T> transactional(Mono<T> publisher);
}
