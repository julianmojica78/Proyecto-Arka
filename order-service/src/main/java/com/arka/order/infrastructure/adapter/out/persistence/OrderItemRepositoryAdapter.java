package com.arka.order.infrastructure.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.arka.order.domain.model.OrderItem;
import com.arka.order.domain.port.out.OrderItemRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryAdapter implements OrderItemRepositoryPort {

    private final SpringOrderItemRepository repository;

    @Override
    public Mono<Void> saveAll(List<OrderItem> items, Long orderId) {

        return Flux.fromIterable(items)
                .map(item -> {
                    OrderItemEntity e = new OrderItemEntity();
                    e.setOrderId(orderId);
                    e.setProductId(item.getProductId());
                    e.setQuantity(item.getQuantity());
                    return e;
                })
                .collectList()
                .flatMapMany(repository::saveAll)
                .then();
    }

    @Override
    public Flux<OrderItem> findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId)
                .map(e -> new OrderItem(
                        e.getProductId(),
                        e.getQuantity()
                ));
    }
}