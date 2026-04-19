package com.arka.order.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.arka.order.domain.exception.OrderException;
import com.arka.order.domain.model.Cart;
import com.arka.order.domain.model.CartItem;
import com.arka.order.domain.port.in.ListAbandonedCartsUseCase;
import com.arka.order.domain.port.in.SaveCartUseCase;
import com.arka.order.domain.port.in.SendCartReminderUseCase;
import com.arka.order.infrastructure.adapter.in.web.dto.CartRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CartHandler {

    private final SaveCartUseCase saveCartUseCase;
    private final ListAbandonedCartsUseCase listAbandonedCartsUseCase;
    private final SendCartReminderUseCase sendCartReminderUseCase;

    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(CartRequest.class)
                .map(this::toCart)
                .flatMap(saveCartUseCase::save)
                .flatMap(cart -> ServerResponse.ok().bodyValue(cart))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ServerResponse> abandoned(ServerRequest request) {
        return ServerResponse.ok().body(listAbandonedCartsUseCase.list(), Cart.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ServerResponse> sendReminder(ServerRequest request) {
        Long cartId = Long.valueOf(request.pathVariable("id"));
        return sendCartReminderUseCase.sendReminder(cartId)
                .then(ServerResponse.ok().bodyValue("recordatorio enviado"))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

	private Cart toCart(CartRequest request) {

		if (request == null) {
			throw new OrderException("Error de validación", "ORDER_001", "El request no puede ser null", "/orders/cart",
					HttpStatus.BAD_REQUEST);
		}

		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new OrderException("Error de validación", "ORDER_002", "Los items son obligatorios", "/orders/cart",
					HttpStatus.BAD_REQUEST);
		}

		Cart cart = new Cart();
		cart.setCustomerId(request.getCustomerId());
		cart.setStatus(request.getStatus());
		cart.setItems(request.getItems().stream().map(item -> new CartItem(item.getProductId(), item.getQuantity()))
				.toList());

		return cart;
	}
}
