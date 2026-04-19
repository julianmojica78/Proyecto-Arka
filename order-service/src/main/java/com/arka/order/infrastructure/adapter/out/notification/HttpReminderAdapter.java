package com.arka.order.infrastructure.adapter.out.notification;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.arka.order.domain.model.Cart;
import com.arka.order.domain.port.out.ReminderPort;

import reactor.core.publisher.Mono;

@Component
public class HttpReminderAdapter implements ReminderPort {

    private final WebClient webClient;

    public HttpReminderAdapter(
            WebClient.Builder webClientBuilder,
            @Value("${notification.service.base-url:http://localhost:8084}") String notificationBaseUrl) {
        this.webClient = webClientBuilder.baseUrl(notificationBaseUrl).build();
    }

    @Override
    public Mono<Void> sendCartReminder(Cart cart) {
        Map<String, Object> body = Map.of(
                "cartId", cart.getId(),
                "customerId", cart.getCustomerId(),
                "items", cart.getItems().stream()
                        .map(item -> Map.of("productId", item.getProductId(), "quantity", item.getQuantity()))
                        .toList());

        return webClient.post()
                .uri("/notifications/cart-reminders")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
