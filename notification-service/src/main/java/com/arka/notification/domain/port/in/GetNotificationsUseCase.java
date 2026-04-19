package com.arka.notification.domain.port.in;

import com.arka.notification.domain.model.NotificationRecord;

import reactor.core.publisher.Flux;

public interface GetNotificationsUseCase {

    Flux<NotificationRecord> findByOrderId(Long orderId);
}
