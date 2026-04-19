package com.arka.notification.domain.port.out;

import com.arka.notification.domain.model.NotificationRecord;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationRepositoryPort {

    Mono<NotificationRecord> save(NotificationRecord notification);

    Flux<NotificationRecord> findByOrderId(Long orderId);
}
