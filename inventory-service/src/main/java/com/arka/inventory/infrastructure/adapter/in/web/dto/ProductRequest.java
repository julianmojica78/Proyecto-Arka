package com.arka.inventory.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
	private String name;
	private String description;
	private BigDecimal price;
	private Integer stock;
	private String category;
}
