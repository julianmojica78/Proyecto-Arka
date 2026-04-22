package com.arka.notification.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Item del carrito usado para construir el recordatorio.")
public class CartReminderItemRequest {

    @Schema(description = "Identificador del producto.", example = "10")
    private Long productId;

    @Schema(description = "Cantidad del producto en el carrito.", example = "2")
    private Integer quantity;
}
