package com.arka.order.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Item solicitado dentro de una orden o carrito.")
public class OrderItemRequest {

    @Schema(description = "Identificador del producto.", example = "10")
    private Long productId;

    @Schema(description = "Cantidad solicitada. Debe ser mayor a cero.", example = "2")
    private Integer quantity;
}
