package com.arka.notification.infrastructure.adapter.in.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class NotificationRouter {

    @Bean
    public RouterFunction<ServerResponse> notificationRoutes(NotificationHandler handler) {
        return RouterFunctions.route()
                .GET("/notifications/orders/{orderId}", handler::getByOrderId)
                .POST("/notifications/cart-reminders", handler::createCartReminder)
                .build();
    }
}
