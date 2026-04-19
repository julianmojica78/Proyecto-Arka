package com.arka.notification.infrastructure.adapter.in.kafka.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockUpdatedEvent extends BaseEvent {

    private String orderId;
}
