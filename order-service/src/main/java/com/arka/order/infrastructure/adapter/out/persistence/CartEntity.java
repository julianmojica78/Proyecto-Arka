package com.arka.order.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.arka.order.domain.model.CartStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("carts")
public class CartEntity {

    @Id
    private Long id;

    @Column("customer_id")
    private String customerId;

    private CartStatus status;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
