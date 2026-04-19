package com.arka.order.application.usecase;

import org.springframework.http.HttpStatus;

import com.arka.order.domain.exception.OrderException;
import com.arka.order.domain.model.Cart;
import com.arka.order.domain.model.CartStatus;
import com.arka.order.domain.port.in.ListAbandonedCartsUseCase;
import com.arka.order.domain.port.in.SaveCartUseCase;
import com.arka.order.domain.port.in.SendCartReminderUseCase;
import com.arka.order.domain.port.out.CartRepositoryPort;
import com.arka.order.domain.port.out.ReminderPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CartService implements SaveCartUseCase, ListAbandonedCartsUseCase, SendCartReminderUseCase {

	private final CartRepositoryPort cartRepository;
	private final ReminderPort reminderPort;

	@Override
	public Mono<Cart> save(Cart cart) {
		if (cart == null) {
			return Mono.error(new OrderException("Error de validación", "ORDER_001", "El carrito no puede ser nulo",
					"/carts", HttpStatus.BAD_REQUEST));
		}

		if (cart.getItems() == null || cart.getItems().isEmpty()) {
			return Mono.error(new OrderException("Error de validación", "ORDER_002", "Los items son obligatorios",
					"/carts", HttpStatus.BAD_REQUEST));
		}

		if (cart.getStatus() == null) {
			cart.setStatus(CartStatus.ACTIVE);
		}

		if (cart.getCustomerId() == null || cart.getCustomerId().isBlank()) {
			cart.setCustomerId("anonymous");
		}

		return cartRepository.save(cart).onErrorMap(ex -> {
			if (ex instanceof OrderException) {
				return ex;
			}
			return new OrderException("Error de persistencia", "ORDER_003",
					"Ocurrió un error al guardar el carrito: " + ex.getMessage(), "/carts",
					HttpStatus.INTERNAL_SERVER_ERROR);
		});
	}

	@Override
	public Flux<Cart> list() {
		return cartRepository.findAbandoned().onErrorMap(ex -> {
			if (ex instanceof OrderException) {
				return ex;
			}
			return new OrderException("Error consultando carritos", "ORDER_004",
					"Ocurrió un error al listar los carritos abandonados: " + ex.getMessage(), "/carts/abandoned",
					HttpStatus.INTERNAL_SERVER_ERROR);
		});
	}

	@Override
	public Mono<Void> sendReminder(Long cartId) {
		if (cartId == null) {
			return Mono.error(new OrderException("Error de validación", "ORDER_005", "El cartId es obligatorio",
					"/carts/reminder", HttpStatus.BAD_REQUEST));
		}

		return cartRepository.findById(cartId)
				.switchIfEmpty(Mono.error(new OrderException("Recurso no encontrado", "ORDER_006",
						"Carrito no encontrado: " + cartId, "/carts/reminder/" + cartId, HttpStatus.NOT_FOUND)))
				.flatMap(reminderPort::sendCartReminder).onErrorMap(ex -> {
					if (ex instanceof OrderException) {
						return ex;
					}
					return new OrderException("Error enviando recordatorio", "ORDER_007",
							"Ocurrió un error al enviar el recordatorio del carrito: " + ex.getMessage(),
							"/carts/reminder/" + cartId, HttpStatus.INTERNAL_SERVER_ERROR);
				});
	}
}