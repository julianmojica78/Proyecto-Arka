package com.arka.inventory.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("products")
public class ProductEntity {

	@Id
	private Long id;
	private String name;
	private String description;
	private BigDecimal price;
	private Integer stock;
	private String category;
}
