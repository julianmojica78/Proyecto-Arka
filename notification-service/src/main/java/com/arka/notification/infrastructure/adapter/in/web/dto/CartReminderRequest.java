package com.arka.notification.infrastructure.adapter.in.web.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartReminderRequest {

    private Long cartId;
    private String customerId;
    private List<CartReminderItemRequest> items;
}
