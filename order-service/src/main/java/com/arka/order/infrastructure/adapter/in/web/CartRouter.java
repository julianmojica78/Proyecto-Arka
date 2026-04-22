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

import com.arka.order.domain.model.Cart;
import com.arka.order.infrastructure.adapter.in.web.dto.CartRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Configuration
public class CartRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/carts",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST,
                    beanClass = CartHandler.class,
                    beanMethod = "save",
                    operation = @Operation(
                            operationId = "saveCart",
                            tags = "Carritos",
                            summary = "Guardar carrito",
                            description = "Guarda o actualiza un carrito con los productos seleccionados por un cliente. Requiere rol CLIENT o ADMIN.",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Cliente, estado e items del carrito.",
                                    content = @Content(schema = @Schema(implementation = CartRequest.class))),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Carrito guardado.",
                                            content = @Content(schema = @Schema(implementation = Cart.class))),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Datos invalidos del carrito.",
                                            content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
                                    @ApiResponse(responseCode = "403", description = "El usuario no tiene un rol permitido.")
                            })),
            @RouterOperation(
                    path = "/carts/abandoned",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET,
                    beanClass = CartHandler.class,
                    beanMethod = "abandoned",
                    operation = @Operation(
                            operationId = "listAbandonedCarts",
                            tags = "Carritos",
                            summary = "Listar carritos abandonados",
                            description = "Retorna carritos abandonados para seguimiento comercial. Requiere rol ADMIN.",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Listado de carritos abandonados.",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Cart.class)))),
                                    @ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
                                    @ApiResponse(responseCode = "403", description = "El usuario no tiene rol ADMIN.")
                            })),
            @RouterOperation(
                    path = "/carts/{id}/reminder",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST,
                    beanClass = CartHandler.class,
                    beanMethod = "sendReminder",
                    operation = @Operation(
                            operationId = "sendCartReminder",
                            tags = "Carritos",
                            summary = "Enviar recordatorio de carrito",
                            description = "Solicita el envio de un recordatorio para un carrito abandonado. Requiere rol ADMIN.",
                            parameters = @Parameter(
                                    name = "id",
                                    in = ParameterIn.PATH,
                                    required = true,
                                    description = "Identificador del carrito.",
                                    schema = @Schema(implementation = Long.class)),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Recordatorio enviado.",
                                            content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "No fue posible enviar el recordatorio.",
                                            content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
                                    @ApiResponse(responseCode = "403", description = "El usuario no tiene rol ADMIN.")
                            }))
    })
    public RouterFunction<ServerResponse> cartRoutes(CartHandler handler) {
        return RouterFunctions.route()
                .POST("/carts", handler::save)
                .GET("/carts/abandoned", handler::abandoned)
                .POST("/carts/{id}/reminder", handler::sendReminder)
                .build();
    }
}
