package com.arka.order.infrastructure.adapter.in.web;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.arka.order.domain.exception.dto.ApiRespuestaExcepcionDTO;
import com.arka.order.domain.model.Order;
import com.arka.order.infrastructure.adapter.in.web.dto.OrderRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Configuration
public class OrderRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/orders",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST,
                    beanClass = OrderHandler.class,
                    beanMethod = "create",
                    operation = @Operation(
                            operationId = "createOrder",
                            tags = "Ordenes",
                            summary = "Crear orden",
                            description = "Crea una orden para un cliente con los productos solicitados. Requiere rol CLIENT o ADMIN.",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Cliente e items de la orden. Cada item requiere productId y quantity mayor a cero.",
                                    content = @Content(schema = @Schema(implementation = OrderRequest.class))),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Orden creada.",
                                            content = @Content(schema = @Schema(implementation = Order.class))),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Datos invalidos en la orden.",
                                            content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
                                    @ApiResponse(responseCode = "403", description = "El usuario no tiene un rol permitido.")
                            })),
            @RouterOperation(
                    path = "/orders/{id}",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.PUT,
                    beanClass = OrderHandler.class,
                    beanMethod = "modify",
                    operation = @Operation(
                            operationId = "modifyOrder",
                            tags = "Ordenes",
                            summary = "Modificar orden",
                            description = "Reemplaza los items de una orden existente. Requiere rol CLIENT o ADMIN.",
                            parameters = @Parameter(
                                    name = "id",
                                    in = ParameterIn.PATH,
                                    required = true,
                                    description = "Identificador de la orden.",
                                    schema = @Schema(implementation = Long.class)),
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Items actualizados de la orden.",
                                    content = @Content(schema = @Schema(implementation = OrderRequest.class))),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Orden modificada.",
                                            content = @Content(schema = @Schema(implementation = Order.class))),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Datos invalidos o error al modificar la orden.",
                                            content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Orden no encontrada.",
                                            content = @Content(schema = @Schema(implementation = ApiRespuestaExcepcionDTO.class))),
                                    @ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
                                    @ApiResponse(responseCode = "403", description = "El usuario no tiene un rol permitido.")
                            })),
            @RouterOperation(
                    path = "/orders/confirmed",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET,
                    beanClass = OrderHandler.class,
                    beanMethod = "listConfirmed",
                    operation = @Operation(
                            operationId = "listConfirmedOrders",
                            tags = "Ordenes",
                            summary = "Listar ordenes confirmadas",
                            description = "Lista ordenes confirmadas en un rango de fechas ISO-8601. Requiere rol ADMIN.",
                            parameters = {
                                    @Parameter(
                                            name = "start",
                                            in = ParameterIn.QUERY,
                                            required = true,
                                            description = "Fecha inicial en formato ISO-8601, por ejemplo 2026-04-01T00:00:00Z.",
                                            schema = @Schema(implementation = String.class)),
                                    @Parameter(
                                            name = "end",
                                            in = ParameterIn.QUERY,
                                            required = true,
                                            description = "Fecha final en formato ISO-8601, por ejemplo 2026-04-08T00:00:00Z.",
                                            schema = @Schema(implementation = String.class))
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Ordenes confirmadas encontradas.",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
                                    @ApiResponse(responseCode = "400", description = "Parametros de fecha ausentes o invalidos."),
                                    @ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
                                    @ApiResponse(responseCode = "403", description = "El usuario no tiene rol ADMIN.")
                            }))
    })
    public RouterFunction<ServerResponse> routes(OrderHandler handler) {
        return RouterFunctions.route()
                .POST("/orders", handler::create)
                .PUT("/orders/{id}", handler::modify)
                .GET("/orders/confirmed", handler::listConfirmed)
                .build();
    }
}
