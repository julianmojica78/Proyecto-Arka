package com.arka.notification.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartReminderCommand {

    private final Long cartId;
    private final String customerId;
    private final List<CartReminderItem> items;
}
