package com.arka.inventory.infrastructure.adapter.in.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockUpdateRequest {

    private Integer stock;
    private String reason;
}
