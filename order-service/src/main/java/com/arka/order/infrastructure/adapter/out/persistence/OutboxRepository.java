package com.arka.order.infrastructure.adapter.out.persistence;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OutboxRepository extends ReactiveCrudRepository<OutboxEventEntity, String> {

    Flux<OutboxEventEntity> findTop100ByStatusOrderByCreatedAtAsc(String status);
    
    @Query("SELECT * FROM outbox WHERE status = :status")
    Flux<OutboxEventEntity> findByStatus(String status);

    @Query("UPDATE outbox SET status = 'SENT' WHERE event_id = :eventId")
    Mono<Void> markAsSent(String eventId);

    @Query("UPDATE outbox SET status = 'FAILED' WHERE event_id = :eventId")
    Mono<Void> markAsFailed(String eventId);
    
    @Query("UPDATE outbox SET retry_count = :retryCount, last_attempt = NOW() WHERE event_id = :eventId")
    Mono<Void> incrementRetry(String eventId, int retryCount);

    Mono<OutboxEventEntity> findFirstByAggregateIdAndEventTypeAndStatus(
            String aggregateId,
            String eventType,
            String status);
}
