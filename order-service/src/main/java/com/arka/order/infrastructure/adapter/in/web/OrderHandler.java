package com.arka.order.infrastructure.adapter.in.web;

import java.time.Instant;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.arka.order.domain.model.CreateOrderCommand;
import com.arka.order.domain.model.ModifyOrderCommand;
import com.arka.order.domain.model.OrderItem;
import com.arka.order.domain.port.in.CreateOrderUseCase;
import com.arka.order.domain.port.in.ListConfirmedOrdersUseCase;
import com.arka.order.domain.port.in.ModifyOrderUseCase;
import com.arka.order.infrastructure.adapter.in.web.dto.OrderRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OrderHandler {

    private final CreateOrderUseCase createOrderUseCase;
    private final ModifyOrderUseCase modifyOrderUseCase;
    private final ListConfirmedOrdersUseCase listConfirmedOrdersUseCase;

    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(OrderRequest.class)
                .flatMap(this::validate)
                .map(this::toCommand)
                .flatMap(createOrderUseCase::create)
                .flatMap(order -> ServerResponse.ok().bodyValue(order))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public Mono<ServerResponse> modify(ServerRequest request) {
        Long orderId = Long.valueOf(request.pathVariable("id"));

        return request.bodyToMono(OrderRequest.class)
                .flatMap(this::validate)
                .map(body -> toModifyCommand(orderId, body))
                .flatMap(modifyOrderUseCase::modify)
                .flatMap(order -> ServerResponse.ok().bodyValue(order))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ServerResponse> listConfirmed(ServerRequest request) {
        Instant start = Instant.parse(request.queryParam("start").orElseThrow());
        Instant end = Instant.parse(request.queryParam("end").orElseThrow());
        return ServerResponse.ok().body(listConfirmedOrdersUseCase.list(start, end), Object.class);
    }

    private Mono<OrderRequest> validate(OrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return Mono.error(new IllegalArgumentException("items son obligatorios"));
        }

        boolean hasInvalidItem = request.getItems().stream()
                .anyMatch(item -> item.getProductId() == null || item.getQuantity() == null || item.getQuantity() <= 0);

        if (hasInvalidItem) {
            return Mono.error(new IllegalArgumentException("cada item debe tener productId y quantity mayor a cero"));
        }

        return Mono.just(request);
    }

    private CreateOrderCommand toCommand(OrderRequest request) {
        return new CreateOrderCommand(request.getCustomerId(), request.getItems().stream()
                .map(item -> new OrderItem(item.getProductId(), item.getQuantity()))
                .toList());
    }

    private ModifyOrderCommand toModifyCommand(Long orderId, OrderRequest request) {
        return new ModifyOrderCommand(orderId, request.getItems().stream()
                .map(item -> new OrderItem(item.getProductId(), item.getQuantity()))
                .toList());
    }
}
