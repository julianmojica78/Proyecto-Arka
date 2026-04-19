package com.arka.order.domain.model;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private Long id;
    private String customerId;
    private OrderStatus status;
    private String correlationId;
    private List<OrderItem> items;
    private Instant createdAt;
    private Instant updatedAt;
}
