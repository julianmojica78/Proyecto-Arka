package com.arka.order.domain.model;

import java.util.List;

public class CreateOrderCommand {

    private final String customerId;
    private final List<OrderItem> items;

    public CreateOrderCommand(String customerId, List<OrderItem> items) {
        this.customerId = customerId;
        this.items = items == null ? List.of() : List.copyOf(items);
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
