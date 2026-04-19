package com.arka.order.domain.port.out;

import com.arka.order.domain.model.OutboxEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OutboxRepositoryPort {
	Mono<OutboxEvent> save(OutboxEvent event);

    Flux<OutboxEvent> findPendingEvents(String status);

    Mono<Void> markAsSent(String eventId);

    Mono<Void> markAsFailed(String eventId);

    Mono<Void> incrementRetry(String eventId, int retryCount);
    
    Flux<OutboxEvent> findAll();

    Mono<OutboxEvent> findPendingByAggregateIdAndEventType(String aggregateId, String eventType, String status);
}
