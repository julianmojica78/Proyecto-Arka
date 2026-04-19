package com.arka.order.application.usecase;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.arka.order.domain.event.OrderCreatedEvent;
import com.arka.order.domain.exception.OrderException;
import com.arka.order.domain.model.ModifyOrderCommand;
import com.arka.order.domain.model.Order;
import com.arka.order.domain.model.OrderItem;
import com.arka.order.domain.model.OrderStatus;
import com.arka.order.domain.model.OutboxEvent;
import com.arka.order.domain.model.OutboxStatus;
import com.arka.order.domain.port.in.ModifyOrderUseCase;
import com.arka.order.domain.port.out.EventSerializerPort;
import com.arka.order.domain.port.out.OrderRepositoryPort;
import com.arka.order.domain.port.out.OutboxRepositoryPort;
import com.arka.order.domain.port.out.TransactionPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ModifyOrderService implements ModifyOrderUseCase {

	private static final String AGGREGATE_TYPE = "ORDER";
	private static final String ORDER_CREATED = "ORDER_CREATED";
	private static final String EVENT_VERSION = "v1";

	private static final String INSTANCE_ORDER = "/orders";
	private static final String INSTANCE_OUTBOX = "/orders/outbox";

	private final OrderRepositoryPort orderRepository;
	private final OutboxRepositoryPort outboxRepository;
	private final EventSerializerPort eventSerializer;
	private final TransactionPort transactionPort;

	@Override
	public Mono<Order> modify(ModifyOrderCommand command) {

		if (command == null) {
			return Mono.error(new OrderException("Error de validación", "ORDER_101", "El comando no puede ser nulo",
					INSTANCE_ORDER, HttpStatus.BAD_REQUEST));
		}

		if (command.getItems() == null || command.getItems().isEmpty()) {
			return Mono.error(new OrderException("Error de validación", "ORDER_102", "Los items son obligatorios",
					INSTANCE_ORDER, HttpStatus.BAD_REQUEST));
		}

		return transactionPort.transactional(orderRepository.findById(command.getOrderId())

				// ❌ antes: OrderNotFoundException
				.switchIfEmpty(Mono.error(new OrderException("Recurso no encontrado", "ORDER_103",
						"Orden no encontrada: " + command.getOrderId(), INSTANCE_ORDER + "/" + command.getOrderId(),
						HttpStatus.NOT_FOUND)))

				// ❌ antes: filter + OrderNotModifiableException
				.flatMap(order -> {
					if (order.getStatus() != OrderStatus.PENDING) {
						return Mono.error(new OrderException("Regla de negocio", "ORDER_104",
								"La orden no se puede modificar en estado: " + order.getStatus(),
								INSTANCE_ORDER + "/" + command.getOrderId(), HttpStatus.CONFLICT));
					}
					return Mono.just(order);
				})

				.flatMap(order -> updateOrderAndOutbox(order, command.getItems())))
				.onErrorMap(ex -> mapToOrderException(ex, "Error modificando orden", "ORDER_105",
						"Ocurrió un error durante la modificación de la orden", INSTANCE_ORDER,
						HttpStatus.INTERNAL_SERVER_ERROR));
	}

	private Mono<Order> updateOrderAndOutbox(Order order, List<OrderItem> items) {

		order.setItems(items);

		return orderRepository.update(order)

				.onErrorMap(ex -> mapToOrderException(ex, "Error actualizando orden", "ORDER_106",
						"Ocurrió un error al actualizar la orden", INSTANCE_ORDER, HttpStatus.INTERNAL_SERVER_ERROR))

				.flatMap(saved -> upsertPendingOutbox(saved, items).thenReturn(saved));
	}

	private Mono<OutboxEvent> upsertPendingOutbox(Order order, List<OrderItem> items) {

		String aggregateId = order.getId().toString();

		return outboxRepository
				.findPendingByAggregateIdAndEventType(aggregateId, ORDER_CREATED, OutboxStatus.PENDING.name())
				.defaultIfEmpty(newOutbox(order))

				.flatMap(outbox -> {
					outbox.setPayload(eventSerializer.serialize(buildEvent(order, items)));
					outbox.setLastAttempt(LocalDateTime.now());
					return outboxRepository.save(outbox);
				})

				.onErrorMap(ex -> mapToOrderException(ex, "Error actualizando outbox", "ORDER_107",
						"Ocurrió un error al actualizar el evento outbox", INSTANCE_OUTBOX,
						HttpStatus.INTERNAL_SERVER_ERROR));
	}

	private OutboxEvent newOutbox(Order order) {
		OutboxEvent outbox = new OutboxEvent();
		outbox.setAggregateId(order.getId().toString());
		outbox.setAggregateType(AGGREGATE_TYPE);
		outbox.setEventType(ORDER_CREATED);
		outbox.setStatus(OutboxStatus.PENDING.name());
		outbox.setCorrelationId(order.getCorrelationId());
		outbox.setRetryCount(0);
		outbox.setCreatedAt(LocalDateTime.now());
		return outbox;
	}

	private OrderCreatedEvent buildEvent(Order order, List<OrderItem> items) {
		return OrderCreatedEvent.builder().eventId(UUID.randomUUID().toString()).correlationId(order.getCorrelationId())
				.type(ORDER_CREATED).eventVersion(EVENT_VERSION).timestamp(Instant.now())
				.orderId(order.getId().toString()).items(items).build();
	}

	private Throwable mapToOrderException(Throwable ex, String titulo, String codigo, String detalleBase,
			String instancia, HttpStatus status) {
		if (ex instanceof OrderException) {
			return ex;
		}

		return new OrderException(titulo, codigo, detalleBase + ": " + ex.getMessage(), instancia, status);
	}
}