package com.arka.inventory.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Datos para ajustar el stock de un producto.")
public class StockUpdateRequest {

    @Schema(description = "Nuevo stock disponible.", example = "80")
    private Integer stock;

    @Schema(description = "Motivo del ajuste de inventario.", example = "Reposicion de inventario")
    private String reason;
}
