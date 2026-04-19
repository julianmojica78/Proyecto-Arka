package com.arka.report.infrastructure.adapter.in.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReportRouter {

    @Bean
    public RouterFunction<ServerResponse> reportRoutes(ReportHandler handler) {
        return RouterFunctions.route()
                .GET("/reports/products/low-stock", handler::lowStock)
                .GET("/reports/sales/weekly", handler::weeklySales)
                .build();
    }
}
