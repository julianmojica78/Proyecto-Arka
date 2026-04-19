package com.arka.order.infrastructure.adapter.in.web.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
	private String customerId;
	private List<OrderItemRequest> items;
}
