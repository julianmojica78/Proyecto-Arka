package com.arka.order.infrastructure.adapter.in.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CartRouter {

    @Bean
    public RouterFunction<ServerResponse> cartRoutes(CartHandler handler) {
        return RouterFunctions.route()
                .POST("/carts", handler::save)
                .GET("/carts/abandoned", handler::abandoned)
                .POST("/carts/{id}/reminder", handler::sendReminder)
                .build();
    }
}
