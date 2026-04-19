package com.arka.order.infrastructure.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProcessedEventRepository extends ReactiveCrudRepository<ProcessedEventEntity, String> {
}