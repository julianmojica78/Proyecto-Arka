package com.arka.inventory.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockItem {

    private final Long productId;
    private final Integer quantity;
}
