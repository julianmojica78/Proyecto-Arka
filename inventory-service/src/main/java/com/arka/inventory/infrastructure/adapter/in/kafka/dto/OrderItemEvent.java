package com.arka.inventory.infrastructure.adapter.in.kafka.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemEvent {

    private Long productId;
    private Integer quantity;
}
