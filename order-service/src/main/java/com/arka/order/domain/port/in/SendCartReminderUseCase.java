package com.arka.order.domain.port.in;

import reactor.core.publisher.Mono;

public interface SendCartReminderUseCase {

    Mono<Void> sendReminder(Long cartId);
}
