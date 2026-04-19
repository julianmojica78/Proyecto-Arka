package com.arka.order.infrastructure.adapter.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("cart_items")
public class CartItemEntity {

    @Id
    private Long id;

    @Column("cart_id")
    private Long cartId;

    @Column("product_id")
    private Long productId;

    private Integer quantity;
}
