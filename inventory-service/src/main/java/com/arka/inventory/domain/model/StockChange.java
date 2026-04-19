package com.arka.inventory.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockChange {

    private Long id;
    private Long productId;
    private Integer previousStock;
    private Integer newStock;
    private String reason;
    private LocalDateTime changedAt;
}
