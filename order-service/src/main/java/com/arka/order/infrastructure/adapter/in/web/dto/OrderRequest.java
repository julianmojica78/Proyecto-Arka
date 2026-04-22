package com.arka.order.infrastructure.adapter.in.web.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Datos para crear o modificar una orden.")
public class OrderRequest {

	@Schema(description = "Identificador del cliente.", example = "cliente-123")
	private String customerId;

	@Schema(description = "Productos incluidos en la orden.")
	private List<OrderItemRequest> items;
}
