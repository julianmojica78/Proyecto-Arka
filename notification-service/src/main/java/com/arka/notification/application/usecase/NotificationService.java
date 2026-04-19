package com.arka.notification.application.usecase;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import com.arka.notification.domain.exception.NotificationException;
import com.arka.notification.domain.model.CartReminderCommand;
import com.arka.notification.domain.model.NotificationRecord;
import com.arka.notification.domain.model.NotificationType;
import com.arka.notification.domain.port.in.CreateNotificationUseCase;
import com.arka.notification.domain.port.in.GetNotificationsUseCase;
import com.arka.notification.domain.port.out.NotificationRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class NotificationService implements CreateNotificationUseCase, GetNotificationsUseCase {

	private final NotificationRepositoryPort repository;

	@Override
	public Mono<NotificationRecord> createOrderConfirmed(Long orderId) {

		if (orderId == null) {
			return Mono.error(new NotificationException("Error de validación", "NOTIF_001",
					"El orderId no puede ser null", "/notifications/order-confirmed", HttpStatus.BAD_REQUEST));
		}

		return repository
				.save(new NotificationRecord(null, orderId, null, null, NotificationType.ORDER_CONFIRMED,
						"Tu pedido fue confirmado", LocalDateTime.now()))
				.onErrorMap(ex -> new NotificationException("Error persistiendo notificación", "NOTIF_002",
						ex.getMessage(), "/notifications/order-confirmed", HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@Override
	public Mono<NotificationRecord> createOrderCancelled(Long orderId, String reason) {

		if (orderId == null) {
			return Mono.error(new NotificationException("Error de validación", "NOTIF_003", "El orderId es obligatorio",
					"/notifications/order-cancelled", HttpStatus.BAD_REQUEST));
		}

		String suffix = (reason == null || reason.isBlank()) ? "" : ": " + reason;

		return repository
				.save(new NotificationRecord(null, orderId, null, null, NotificationType.ORDER_CANCELLED,
						"Tu pedido fue cancelado" + suffix, LocalDateTime.now()))
				.onErrorMap(ex -> new NotificationException("Error persistiendo cancelación", "NOTIF_004",
						ex.getMessage(), "/notifications/order-cancelled", HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@Override
	public Mono<NotificationRecord> createCartReminder(CartReminderCommand command) {

		if (command == null || command.getItems() == null || command.getItems().isEmpty()) {
			return Mono.error(new NotificationException("Error de validación", "NOTIF_005", "El carrito no tiene items",
					"/notifications/cart-reminder", HttpStatus.BAD_REQUEST));
		}

		String itemsText = command.getItems().stream().map(item -> item.getProductId() + "x" + item.getQuantity())
				.collect(Collectors.joining("; "));

		return repository.save(new NotificationRecord(null, null, command.getCartId(), command.getCustomerId(),
				NotificationType.CART_REMINDER, "Recordatorio de carrito pendiente: " + itemsText, LocalDateTime.now()))
				.onErrorMap(ex -> new NotificationException("Error guardando recordatorio", "NOTIF_006",
						ex.getMessage(), "/notifications/cart-reminder", HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@Override
	public Flux<NotificationRecord> findByOrderId(Long orderId) {

		if (orderId == null) {
			return Flux.error(new NotificationException("Error de validación", "NOTIF_007", "orderId es obligatorio",
					"/notifications/find", HttpStatus.BAD_REQUEST));
		}

		return repository.findByOrderId(orderId).switchIfEmpty(Flux.error(new NotificationException("No encontrado",
				"NOTIF_008", "No hay notificaciones para la orden", "/notifications/find", HttpStatus.NOT_FOUND)));
	}
}
