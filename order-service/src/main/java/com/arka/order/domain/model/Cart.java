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
public class Cart {

    private Long id;
    private String customerId;
    private CartStatus status;
    private List<CartItem> items;
    private Instant createdAt;
    private Instant updatedAt;
}
