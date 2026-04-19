package com.arka.order.application.usecase;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.arka.order.domain.event.OrderCreatedEvent;
import com.arka.order.domain.exception.OrderException;
import com.arka.order.domain.model.CreateOrderCommand;
import com.arka.order.domain.model.Order;
import com.arka.order.domain.model.OrderItem;
import com.arka.order.domain.model.OrderStatus;
import com.arka.order.domain.model.OutboxEvent;
import com.arka.order.domain.model.OutboxStatus;
import com.arka.order.domain.port.in.CreateOrderUseCase;
import com.arka.order.domain.port.out.EventSerializerPort;
import com.arka.order.domain.port.out.OrderRepositoryPort;
import com.arka.order.domain.port.out.OutboxRepositoryPort;
import com.arka.order.domain.port.out.TransactionPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {

	private static final String AGGREGATE_TYPE = "ORDER";
	private static final String ORDER_CREATED = "ORDER_CREATED";
	private static final String EVENT_VERSION = "v1";

	private static final String INSTANCE_CREATE_ORDER = "/orders";
	private static final String INSTANCE_OUTBOX = "/orders/outbox";

	private final OrderRepositoryPort repository;
	private final OutboxRepositoryPort outboxRepository;
	private final EventSerializerPort eventSerializer;
	private final TransactionPort transactionPort;

	@Override
	public Mono<Order> create(CreateOrderCommand command) {

		if (command == null) {
			return Mono.error(new OrderException("Error de validación", "ORDER_001",
					"El comando de creación de orden no puede ser nulo", INSTANCE_CREATE_ORDER,
					HttpStatus.BAD_REQUEST));
		}

		List<OrderItem> items = command.getItems();

		if (items == null || items.isEmpty()) {
			return Mono.error(new OrderException("Error de validación", "ORDER_002", "Los items son obligatorios",
					INSTANCE_CREATE_ORDER, HttpStatus.BAD_REQUEST));
		}

		String correlationId = UUID.randomUUID().toString();

		Order order = new Order();
		order.setCustomerId(normalizeCustomer(command.getCustomerId()));
		order.setStatus(OrderStatus.PENDING);
		order.setCorrelationId(correlationId);
		order.setItems(items);

		log.info("[{}] Starting order creation", correlationId);

		return transactionPort.transactional(repository.save(order)
				.onErrorMap(ex -> mapToOrderException(ex, "Error persistiendo la orden", "ORDER_003",
						"Ocurrió un error al guardar la orden", INSTANCE_CREATE_ORDER,
						HttpStatus.INTERNAL_SERVER_ERROR))
				.flatMap(saved -> saveOutboxEvent(buildEvent(saved, items, correlationId), saved.getId(), correlationId)
						.thenReturn(saved)))
				.onErrorMap(ex -> {
					if (ex instanceof OrderException) {
						return ex;
					}
					return new OrderException("Error creando la orden", "ORDER_004",
							"Ocurrió un error durante la transacción de creación de la orden: " + ex.getMessage(),
							INSTANCE_CREATE_ORDER, HttpStatus.INTERNAL_SERVER_ERROR);
				}).doOnSuccess(saved -> log.info("[{}] Order transaction completed", correlationId))
				.doOnError(error -> log.error("[{}] Error creating order", correlationId, error));
	}

	private Mono<OutboxEvent> saveOutboxEvent(OrderCreatedEvent event, Long orderId, String correlationId) {
		return Mono.fromCallable(() -> {
			OutboxEvent outbox = new OutboxEvent();
			outbox.setAggregateId(orderId.toString());
			outbox.setAggregateType(AGGREGATE_TYPE);
			outbox.setEventType(ORDER_CREATED);
			outbox.setPayload(eventSerializer.serialize(event));
			outbox.setStatus(OutboxStatus.PENDING.name());
			outbox.setCorrelationId(correlationId);
			outbox.setRetryCount(0);
			outbox.setLastAttempt(LocalDateTime.now());
			outbox.setCreatedAt(LocalDateTime.now());
			return outbox;
		}).onErrorMap(ex -> mapToOrderException(ex, "Error serializando evento", "ORDER_005",
				"Ocurrió un error al construir o serializar el evento de outbox", INSTANCE_OUTBOX,
				HttpStatus.INTERNAL_SERVER_ERROR)).flatMap(outboxRepository::save)
				.onErrorMap(ex -> mapToOrderException(ex, "Error persistiendo outbox", "ORDER_006",
						"Ocurrió un error al guardar el evento en outbox", INSTANCE_OUTBOX,
						HttpStatus.INTERNAL_SERVER_ERROR))
				.doOnNext(saved -> log.info("[{}] Outbox saved: {}", correlationId, saved.getEventId()));
	}

	private OrderCreatedEvent buildEvent(Order order, List<OrderItem> items, String correlationId) {
		return OrderCreatedEvent.builder().eventId(UUID.randomUUID().toString()).correlationId(correlationId)
				.type(ORDER_CREATED).eventVersion(EVENT_VERSION).timestamp(Instant.now())
				.orderId(order.getId().toString()).items(items).build();
	}

	private String normalizeCustomer(String customerId) {
		return customerId == null || customerId.isBlank() ? "anonymous" : customerId;
	}

	private Throwable mapToOrderException(Throwable ex, String titulo, String codigo, String detalleBase,
			String instancia, HttpStatus status) {
		if (ex instanceof OrderException) {
			return ex;
		}

		return new OrderException(titulo, codigo, detalleBase + ": " + ex.getMessage(), instancia, status);
	}
}