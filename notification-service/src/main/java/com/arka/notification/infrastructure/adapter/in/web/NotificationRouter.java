package com.arka.notification.infrastructure.adapter.in.web;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.arka.notification.domain.model.NotificationRecord;
import com.arka.notification.infrastructure.adapter.in.web.dto.CartReminderRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Configuration
public class NotificationRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/notifications/orders/{orderId}",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET,
                    beanClass = NotificationHandler.class,
                    beanMethod = "getByOrderId",
                    operation = @Operation(
                            operationId = "getNotificationsByOrderId",
                            tags = "Notificaciones",
                            summary = "Consultar notificaciones por orden",
                            description = "Retorna las notificaciones asociadas a una orden. Requiere rol ADMIN.",
                            parameters = @Parameter(
                                    name = "orderId",
                                    in = ParameterIn.PATH,
                                    required = true,
                                    description = "Identificador de la orden.",
                                    schema = @Schema(implementation = Long.class)),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Notificaciones encontradas.",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationRecord.class)))),
                                    @ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
                                    @ApiResponse(responseCode = "403", description = "El usuario no tiene rol ADMIN.")
                            })),
            @RouterOperation(
                    path = "/notifications/cart-reminders",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST,
                    beanClass = NotificationHandler.class,
                    beanMethod = "createCartReminder",
                    operation = @Operation(
                            operationId = "createCartReminderNotification",
                            tags = "Notificaciones",
                            summary = "Crear notificacion de recordatorio de carrito",
                            description = "Crea una notificacion para recordar un carrito abandonado. Requiere rol ADMIN.",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos del carrito y sus items para construir el recordatorio.",
                                    content = @Content(schema = @Schema(implementation = CartReminderRequest.class))),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Notificacion creada.",
                                            content = @Content(schema = @Schema(implementation = NotificationRecord.class))),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Datos invalidos para crear el recordatorio.",
                                            content = @Content(schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
                                    @ApiResponse(responseCode = "403", description = "El usuario no tiene rol ADMIN.")
                            }))
    })
    public RouterFunction<ServerResponse> notificationRoutes(NotificationHandler handler) {
        return RouterFunctions.route()
                .GET("/notifications/orders/{orderId}", handler::getByOrderId)
                .POST("/notifications/cart-reminders", handler::createCartReminder)
                .build();
    }
}
