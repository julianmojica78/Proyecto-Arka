package com.arka.notification.infrastructure.adapter.in.web;

import com.arka.notification.domain.model.CartReminderCommand;
import com.arka.notification.domain.model.CartReminderItem;
import com.arka.notification.domain.port.in.CreateNotificationUseCase;
import com.arka.notification.domain.port.in.GetNotificationsUseCase;
import com.arka.notification.infrastructure.adapter.in.web.dto.CartReminderRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NotificationHandler {

    private final CreateNotificationUseCase createNotificationUseCase;
    private final GetNotificationsUseCase getNotificationsUseCase;

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ServerResponse> getByOrderId(ServerRequest request) {
        Long orderId = Long.valueOf(request.pathVariable("orderId"));
        return ServerResponse.ok().body(getNotificationsUseCase.findByOrderId(orderId), Object.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ServerResponse> createCartReminder(ServerRequest request) {
        return request.bodyToMono(CartReminderRequest.class)
                .map(body -> new CartReminderCommand(
                        body.getCartId(),
                        body.getCustomerId(),
                        body.getItems().stream()
                                .map(item -> new CartReminderItem(item.getProductId(), item.getQuantity()))
                                .toList()))
                .flatMap(createNotificationUseCase::createCartReminder)
                .flatMap(notification -> ServerResponse.ok().bodyValue(notification))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }
}
