package com.arka.inventory.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("stock_history")
public class StockChangeEntity {

    @Id
    private Long id;

    @Column("product_id")
    private Long productId;

    @Column("previous_stock")
    private Integer previousStock;

    @Column("new_stock")
    private Integer newStock;

    private String reason;

    @Column("changed_at")
    private LocalDateTime changedAt;
}
