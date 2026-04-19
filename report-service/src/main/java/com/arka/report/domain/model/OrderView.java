package com.arka.report.domain.model;

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
public class OrderView {

    private Long id;
    private String customerId;
    private String status;
    private List<OrderItemView> items;
    private Instant createdAt;
}
