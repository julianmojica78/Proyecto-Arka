package com.arka.order.infrastructure.adapter.out.persistence;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Repository;

import com.arka.order.domain.model.Order;
import com.arka.order.domain.model.OrderItem;
import com.arka.order.domain.model.OrderStatus;
import com.arka.order.domain.port.out.OrderRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

	private final SpringOrderRepository orderRepository;
	private final SpringOrderItemRepository orderItemRepository;

	@Override
	public Mono<Order> save(Order order) {

		OrderEntity entity = toEntity(order);

		return orderRepository.save(entity)
				.flatMap(savedEntity -> saveItems(savedEntity.getId(), order).thenReturn(savedEntity))
				.flatMap(this::toDomainWithItems);
	}

	// 🔥 FIND BY ID (incluye items)
	@Override
	public Mono<Order> findById(Long id) {
		return orderRepository.findById(id).flatMap(this::toDomainWithItems);
	}

	// 🔥 UPDATE (reutiliza lógica para evitar inconsistencias)
	@Override
	public Mono<Order> update(Order order) {
		return save(order);
	}

	@Override
	public Flux<Order> findByStatusAndCreatedAtBetween(OrderStatus status, Instant start, Instant end) {
		return orderRepository.findByStatusAndCreatedAtBetween(status, toLocalDateTime(start), toLocalDateTime(end))
				.flatMap(this::toDomainWithItems);
	}

	// ==============================
	// 🔧 MÉTODOS PRIVADOS
	// ==============================

	private Mono<Void> saveItems(Long orderId, Order order) {

		if (order.getItems() == null || order.getItems().isEmpty()) {
			return Mono.empty();
		}

		return orderItemRepository.deleteByOrderId(orderId).thenMany(Flux.fromIterable(order.getItems()).map(item -> {
			OrderItemEntity e = new OrderItemEntity();
			e.setOrderId(orderId);
			e.setProductId(item.getProductId());
			e.setQuantity(item.getQuantity());
			return e;
		})).flatMap(orderItemRepository::save).then();
	}

	private Mono<Order> toDomainWithItems(OrderEntity entity) {

		return orderItemRepository.findByOrderId(entity.getId())
				.map(item -> new OrderItem(item.getProductId(), item.getQuantity())).collectList().map(items -> {
					Order order = toDomain(entity);
					order.setItems(items);
					return order;
				});
	}

	private OrderEntity toEntity(Order order) {

		OrderEntity entity = new OrderEntity();

		entity.setId(order.getId());
		entity.setCustomerId(order.getCustomerId());
		entity.setStatus(order.getStatus());
		entity.setCorrelationId(order.getCorrelationId());

		entity.setCreatedAt(order.getCreatedAt() != null ? toLocalDateTime(order.getCreatedAt()) : LocalDateTime.now());

		entity.setUpdatedAt(LocalDateTime.now());

		return entity;
	}

	private Order toDomain(OrderEntity entity) {

		Order order = new Order();

		order.setId(entity.getId());
		order.setCustomerId(entity.getCustomerId());
		order.setStatus(entity.getStatus());
		order.setCorrelationId(entity.getCorrelationId());

		order.setCreatedAt(toInstant(entity.getCreatedAt()));
		order.setUpdatedAt(toInstant(entity.getUpdatedAt()));

		return order;
	}

	private LocalDateTime toLocalDateTime(Instant instant) {
		return instant != null ? LocalDateTime.ofInstant(instant, ZoneId.systemDefault()) : LocalDateTime.now();
	}

	private Instant toInstant(LocalDateTime dateTime) {
		return dateTime != null ? dateTime.atZone(ZoneId.systemDefault()).toInstant() : null;
	}
}
