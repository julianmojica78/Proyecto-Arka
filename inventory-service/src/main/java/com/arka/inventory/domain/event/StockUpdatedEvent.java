package com.arka.inventory.domain.event;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockUpdatedEvent {

    private final String eventId;
    private final String correlationId;
    private final String type;
    private final Instant timestamp;
    private final String eventVersion;
    private final String orderId;
}
