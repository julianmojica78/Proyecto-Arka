package com.arka.order.domain.event;

import java.util.List;

import com.arka.order.domain.model.OrderItem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class OrderCreatedEvent extends BaseEvent {

    private String orderId;
    private List<OrderItem> items;
}
