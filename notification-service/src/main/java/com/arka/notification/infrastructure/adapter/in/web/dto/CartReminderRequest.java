package com.arka.notification.infrastructure.adapter.in.web.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Datos para crear una notificacion de recordatorio de carrito.")
public class CartReminderRequest {

    @Schema(description = "Identificador del carrito abandonado.", example = "25")
    private Long cartId;

    @Schema(description = "Identificador del cliente asociado al carrito.", example = "cliente-123")
    private String customerId;

    @Schema(description = "Items incluidos en el carrito.")
    private List<CartReminderItemRequest> items;
}
