package com.arka.inventory.domain.port.out;

import com.arka.inventory.domain.event.StockFailedEvent;
import com.arka.inventory.domain.event.StockUpdatedEvent;

import reactor.core.publisher.Mono;

public interface StockEventPublisherPort {

    Mono<Void> publishStockUpdated(StockUpdatedEvent event);

    Mono<Void> publishStockFailed(StockFailedEvent event);
}
