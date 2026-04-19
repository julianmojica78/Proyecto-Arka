package com.arka.order.domain.event;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseEvent {

    private String eventId;
    private String correlationId;
    private String type;
    private Instant timestamp;
    private String eventVersion;
}
