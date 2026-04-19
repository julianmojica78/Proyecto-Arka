package com.arka.order.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.arka.order.domain.model.OutboxEvent;
import com.arka.order.domain.port.out.OutboxRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class OutboxRepositoryAdapter implements OutboxRepositoryPort {

    private final OutboxRepository repository;

    @Override
    public Mono<OutboxEvent> save(OutboxEvent event) {
        return repository.save(toEntity(event)).map(this::toDomain);
    }

    @Override
    public Flux<OutboxEvent> findPendingEvents(String status) {
        return repository.findTop100ByStatusOrderByCreatedAtAsc(status).map(this::toDomain);
    }

    @Override
    public Mono<Void> markAsSent(String eventId) {
        return repository.markAsSent(eventId);
    }

    @Override
    public Mono<Void> markAsFailed(String eventId) {
        return repository.markAsFailed(eventId);
    }

    @Override
    public Mono<Void> incrementRetry(String eventId, int retryCount) {
        return repository.incrementRetry(eventId, retryCount);
    }

    @Override
    public Flux<OutboxEvent> findAll() {
        return repository.findAll().map(this::toDomain);
    }

    @Override
    public Mono<OutboxEvent> findPendingByAggregateIdAndEventType(String aggregateId, String eventType, String status) {
        return repository.findFirstByAggregateIdAndEventTypeAndStatus(aggregateId, eventType, status)
                .map(this::toDomain);
    }

    private OutboxEvent toDomain(OutboxEventEntity entity) {
        OutboxEvent event = new OutboxEvent();
        event.setEventId(entity.getEventId());
        event.setAggregateId(entity.getAggregateId());
        event.setAggregateType(entity.getAggregateType());
        event.setEventType(entity.getEventType());
        event.setPayload(entity.getPayload());
        event.setStatus(entity.getStatus());
        event.setCorrelationId(entity.getCorrelationId());
        event.setCreatedAt(entity.getCreatedAt());
        event.setRetryCount(entity.getRetryCount());
        event.setLastAttempt(entity.getLastAttempt());
        return event;
    }

    private OutboxEventEntity toEntity(OutboxEvent event) {
        OutboxEventEntity entity = new OutboxEventEntity();
        entity.setEventId(event.getEventId());
        entity.setAggregateId(event.getAggregateId());
        entity.setAggregateType(event.getAggregateType());
        entity.setEventType(event.getEventType());
        entity.setPayload(event.getPayload());
        entity.setStatus(event.getStatus());
        entity.setCorrelationId(event.getCorrelationId());
        entity.setCreatedAt(event.getCreatedAt());
        entity.setRetryCount(event.getRetryCount());
        entity.setLastAttempt(event.getLastAttempt());
        return entity;
    }
}
