package com.arka.auth.infrastructure.adapter.in.web;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.arka.auth.domain.exception.dto.ApiRespuestaExcepcionDTO;
import com.arka.auth.infrastructure.adapter.in.web.dto.LoginRequest;
import com.arka.auth.infrastructure.adapter.in.web.dto.LoginResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Configuration
public class AuthRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/auth/login",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST,
                    beanClass = AuthHandler.class,
                    beanMethod = "login",
                    operation = @Operation(
                            operationId = "login",
                            tags = "Autenticacion",
                            summary = "Iniciar sesion",
                            description = "Valida las credenciales del usuario y retorna un token JWT tipo Bearer.",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Credenciales del usuario.",
                                    content = @Content(schema = @Schema(implementation = LoginRequest.class))),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Autenticacion exitosa. Retorna el token JWT.",
                                            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Credenciales invalidas.",
                                            content = @Content(schema = @Schema(implementation = ApiRespuestaExcepcionDTO.class))),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno inesperado.",
                                            content = @Content(schema = @Schema(implementation = ApiRespuestaExcepcionDTO.class)))
                            })),
            @RouterOperation(
                    path = "/auth/health",
                    produces = MediaType.TEXT_PLAIN_VALUE,
                    method = RequestMethod.GET,
                    beanClass = AuthHandler.class,
                    beanMethod = "health",
                    operation = @Operation(
                            operationId = "authHealth",
                            tags = "Autenticacion",
                            summary = "Verificar disponibilidad de auth-service",
                            description = "Endpoint publico de salud basico del servicio de autenticacion.",
                            responses = @ApiResponse(
                                    responseCode = "200",
                                    description = "Servicio disponible.",
                                    content = @Content(schema = @Schema(implementation = String.class)))))
    })
    public RouterFunction<ServerResponse> routes(AuthHandler handler) {
        return RouterFunctions.route()
                .POST("/auth/login", handler::login)
                .GET("/auth/health", handler::health)
                .build();
    }
}
