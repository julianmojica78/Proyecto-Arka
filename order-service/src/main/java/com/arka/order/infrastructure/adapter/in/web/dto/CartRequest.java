package com.arka.order.infrastructure.adapter.in.web.dto;

import java.util.List;

import com.arka.order.domain.model.CartStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Datos para guardar un carrito.")
public class CartRequest {

    @Schema(description = "Identificador del cliente.", example = "cliente-123")
    private String customerId;

    @Schema(description = "Estado del carrito.", example = "ABANDONED")
    private CartStatus status;

    @Schema(description = "Productos incluidos en el carrito.")
    private List<OrderItemRequest> items;
}
