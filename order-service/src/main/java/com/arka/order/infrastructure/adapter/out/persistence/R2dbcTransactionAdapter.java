package com.arka.order.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.arka.order.domain.port.out.TransactionPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class R2dbcTransactionAdapter implements TransactionPort {

    private final TransactionalOperator transactionalOperator;

    @Override
    public <T> Mono<T> transactional(Mono<T> publisher) {
        return transactionalOperator.transactional(publisher);
    }
}
