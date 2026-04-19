package com.arka.order.application.usecase;

import java.time.Instant;

import com.arka.order.domain.model.Order;
import com.arka.order.domain.model.OrderStatus;
import com.arka.order.domain.port.in.ListConfirmedOrdersUseCase;
import com.arka.order.domain.port.out.OrderRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class ListConfirmedOrdersService implements ListConfirmedOrdersUseCase {

    private final OrderRepositoryPort orderRepository;

    @Override
    public Flux<Order> list(Instant start, Instant end) {
        return orderRepository.findByStatusAndCreatedAtBetween(OrderStatus.CONFIRMED, start, end);
    }
}
