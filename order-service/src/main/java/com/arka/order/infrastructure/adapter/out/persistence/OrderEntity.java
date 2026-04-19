package com.arka.order.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.arka.order.domain.model.OrderStatus;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Table("orders")
public class OrderEntity {

    @Id
    private Long id;
    private String customerId;
    private OrderStatus status;
    private String correlationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
