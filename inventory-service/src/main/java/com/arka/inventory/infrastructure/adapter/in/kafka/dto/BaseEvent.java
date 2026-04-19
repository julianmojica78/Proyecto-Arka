package com.arka.inventory.infrastructure.adapter.in.kafka.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseEvent {

    private String eventId;
    private String correlationId;
    private String type;
    private Instant timestamp;
    private String eventVersion;
}
