package com.arka.order.domain.model;

import java.util.List;

public class ModifyOrderCommand {

    private final Long orderId;
    private final List<OrderItem> items;

    public ModifyOrderCommand(Long orderId, List<OrderItem> items) {
        this.orderId = orderId;
        this.items = items == null ? List.of() : List.copyOf(items);
    }

    public Long getOrderId() {
        return orderId;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
