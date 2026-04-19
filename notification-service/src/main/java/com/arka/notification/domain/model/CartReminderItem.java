package com.arka.notification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartReminderItem {

    private final Long productId;
    private final Integer quantity;
}
