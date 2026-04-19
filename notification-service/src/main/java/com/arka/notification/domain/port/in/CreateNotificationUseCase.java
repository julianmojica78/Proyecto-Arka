package com.arka.notification.domain.port.in;

import com.arka.notification.domain.model.CartReminderCommand;
import com.arka.notification.domain.model.NotificationRecord;

import reactor.core.publisher.Mono;

public interface CreateNotificationUseCase {

    Mono<NotificationRecord> createOrderConfirmed(Long orderId);

    Mono<NotificationRecord> createOrderCancelled(Long orderId, String reason);

    Mono<NotificationRecord> createCartReminder(CartReminderCommand command);
}
