package com.arka.order.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Repository;

import com.arka.order.domain.port.out.ProcessedEventPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ProcessedEventRepositoryAdapter implements ProcessedEventPort {

    private final ProcessedEventRepository repository;

    @Override
    public Mono<Boolean> existsById(String eventId) {
        return repository.existsById(eventId);
    }

    @Override
    public Mono<Void> markProcessed(String eventId) {
        ProcessedEventEntity entity = new ProcessedEventEntity();
        entity.setEventId(eventId);
        return repository.save(entity).then();
    }
}
