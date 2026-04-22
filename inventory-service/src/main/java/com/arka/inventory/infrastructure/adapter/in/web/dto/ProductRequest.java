package com.arka.inventory.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Datos necesarios para crear un producto.")
public class ProductRequest {

	@Schema(description = "Nombre comercial del producto.", example = "Laptop")
	private String name;

	@Schema(description = "Descripcion visible del producto.", example = "Laptop Acer")
	private String description;

	@Schema(description = "Precio unitario del producto.", example = "24900.00")
	private BigDecimal price;

	@Schema(description = "Cantidad inicial disponible en inventario.", example = "120")
	private Integer stock;

	@Schema(description = "Categoria del producto.", example = "Portatiles")
	private String category;
}
