package com.arka.order.infrastructure.adapter.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("order_items")
public class OrderItemEntity {

    @Id
    private Long id;

    private Long orderId;
    private Long productId;
    private Integer quantity;
}