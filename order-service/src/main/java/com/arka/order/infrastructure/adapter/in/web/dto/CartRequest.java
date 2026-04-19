package com.arka.order.infrastructure.adapter.in.web.dto;

import java.util.List;

import com.arka.order.domain.model.CartStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequest {

    private String customerId;
    private CartStatus status;
    private List<OrderItemRequest> items;
}
