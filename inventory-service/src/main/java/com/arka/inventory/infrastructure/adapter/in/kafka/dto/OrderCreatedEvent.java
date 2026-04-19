package com.arka.inventory.infrastructure.adapter.in.kafka.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreatedEvent extends BaseEvent {

    private String orderId;
    private List<OrderItemEvent> items;
}
