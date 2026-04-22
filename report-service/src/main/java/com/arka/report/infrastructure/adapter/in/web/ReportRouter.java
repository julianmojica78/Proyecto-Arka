package com.arka.report.infrastructure.adapter.in.web;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Configuration
public class ReportRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/reports/products/low-stock",
                    produces = "text/csv",
                    method = RequestMethod.GET,
                    beanClass = ReportHandler.class,
                    beanMethod = "lowStock",
                    operation = @Operation(
                            operationId = "generateLowStockReport",
                            tags = "Reportes",
                            summary = "Generar reporte de bajo stock",
                            description = "Genera un archivo CSV con productos cuyo stock este por debajo del umbral indicado. Requiere rol ADMIN.",
                            parameters = @Parameter(
                                    name = "threshold",
                                    in = ParameterIn.QUERY,
                                    required = false,
                                    description = "Umbral maximo de stock para incluir productos en el reporte.",
                                    schema = @Schema(implementation = Integer.class)),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "CSV generado. Incluye Content-Disposition con filename=low-stock-report.csv.",
                                            content = @Content(mediaType = "text/csv", schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
                                    @ApiResponse(responseCode = "403", description = "El usuario no tiene rol ADMIN.")
                            })),
            @RouterOperation(
                    path = "/reports/sales/weekly",
                    produces = "text/csv",
                    method = RequestMethod.GET,
                    beanClass = ReportHandler.class,
                    beanMethod = "weeklySales",
                    operation = @Operation(
                            operationId = "generateWeeklySalesReport",
                            tags = "Reportes",
                            summary = "Generar reporte semanal de ventas",
                            description = "Genera un archivo CSV con el resumen semanal de ventas. Requiere rol ADMIN.",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "CSV generado. Incluye Content-Disposition con filename=weekly-sales-report.csv.",
                                            content = @Content(mediaType = "text/csv", schema = @Schema(implementation = String.class))),
                                    @ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido."),
                                    @ApiResponse(responseCode = "403", description = "El usuario no tiene rol ADMIN.")
                            }))
    })
    public RouterFunction<ServerResponse> reportRoutes(ReportHandler handler) {
        return RouterFunctions.route()
                .GET("/reports/products/low-stock", handler::lowStock)
                .GET("/reports/sales/weekly", handler::weeklySales)
                .build();
    }
}
