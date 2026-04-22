package com.arka.inventory.infrastructure.adapter.in.web;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.arka.inventory.domain.exception.dto.ApiRespuestaExcepcionDTO;
import com.arka.inventory.domain.model.Product;
import com.arka.inventory.infrastructure.adapter.in.web.dto.ProductRequest;
import com.arka.inventory.infrastructure.adapter.in.web.dto.StockUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Configuration
public class ProductRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/products",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST,
                    beanClass = ProductHandler.class,
                    beanMethod = "create",
                    operation = @Operation(
                            operationId = "createProduct",
                            tags = "Productos e inventario",
                            summary = "Crear producto",
                            description = "Crea un producto con su precio, categoria y stock inicial. Requiere rol ADMIN.",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos del producto a crear.",
                                    content = @Content(schema = @Schema(implementation = ProductRequest.class))),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Producto creado.",
                                            content = @Content(schema = @Schema(implementation = Product.class))),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Datos del producto invalidos.",
                                            content = @Content(schema = @Schema(implementation = ApiRespuestaExcepcionDTO.class))),
                                    @ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
                                    @ApiResponse(responseCode = "403", description = "El usuario no tiene rol ADMIN.")
                            })),
            @RouterOperation(
                    path = "/products",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET,
                    beanClass = ProductHandler.class,
                    beanMethod = "getAll",
                    operation = @Operation(
                            operationId = "getProducts",
                            tags = "Productos e inventario",
                            summary = "Listar productos",
                            description = "Retorna los productos disponibles en inventario. Requiere rol ADMIN o CLIENT.",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Listado de productos.",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
                                    @ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
                                    @ApiResponse(responseCode = "403", description = "El usuario no tiene un rol permitido.")
                            })),
            @RouterOperation(
                    path = "/products/{id}/stock",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateStock",
                    operation = @Operation(
                            operationId = "updateProductStock",
                            tags = "Productos e inventario",
                            summary = "Actualizar stock de producto",
                            description = "Actualiza el stock de un producto y registra la razon del cambio. Requiere rol ADMIN.",
                            parameters = @Parameter(
                                    name = "id",
                                    in = ParameterIn.PATH,
                                    required = true,
                                    description = "Identificador del producto.",
                                    schema = @Schema(implementation = Long.class)),
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Nuevo stock y razon del ajuste.",
                                    content = @Content(schema = @Schema(implementation = StockUpdateRequest.class))),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Stock actualizado.",
                                            content = @Content(schema = @Schema(implementation = Product.class))),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Solicitud invalida.",
                                            content = @Content(schema = @Schema(implementation = ApiRespuestaExcepcionDTO.class))),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Producto no encontrado.",
                                            content = @Content(schema = @Schema(implementation = ApiRespuestaExcepcionDTO.class))),
                                    @ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
                                    @ApiResponse(responseCode = "403", description = "El usuario no tiene rol ADMIN.")
                            }))
    })
    public RouterFunction<ServerResponse> routes(ProductHandler handler) {
        return RouterFunctions.route()
                .POST("/products", handler::create)
                .GET("/products", handler::getAll)
                .PATCH("/products/{id}/stock", handler::updateStock)
                .build();
    }
}
