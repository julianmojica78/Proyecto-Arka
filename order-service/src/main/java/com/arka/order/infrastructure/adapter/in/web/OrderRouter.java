package com.arka.order.infrastructure.adapter.in.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class OrderRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(OrderHandler handler) {
        return RouterFunctions.route()
                .POST("/orders", handler::create)
                .PUT("/orders/{id}", handler::modify)
                .GET("/orders/confirmed", handler::listConfirmed)
                .build();
    }
}
