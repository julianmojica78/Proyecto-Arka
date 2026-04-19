package com.arka.notification.infrastructure.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface NotificationRepository extends ReactiveCrudRepository<NotificationEntity, Long> {

    Flux<NotificationEntity> findByOrderIdOrderByCreatedAtDesc(Long orderId);
}
