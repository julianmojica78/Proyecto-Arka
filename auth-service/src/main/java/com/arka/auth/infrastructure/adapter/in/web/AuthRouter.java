package com.arka.auth.infrastructure.adapter.in.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AuthRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(AuthHandler handler) {
        return RouterFunctions.route()
                .POST("/auth/login", handler::login)
                .GET("/auth/health", handler::health)
                .build();
    }
}