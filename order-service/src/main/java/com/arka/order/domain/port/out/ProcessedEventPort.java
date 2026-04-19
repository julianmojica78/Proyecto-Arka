package com.arka.order.domain.port.out;

import reactor.core.publisher.Mono;

public interface ProcessedEventPort {

    Mono<Boolean> existsById(String eventId);

    Mono<Void> markProcessed(String eventId);
    
}
