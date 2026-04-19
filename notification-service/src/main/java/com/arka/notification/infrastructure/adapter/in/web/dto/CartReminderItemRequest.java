package com.arka.notification.infrastructure.adapter.in.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartReminderItemRequest {

    private Long productId;
    private Integer quantity;
}
