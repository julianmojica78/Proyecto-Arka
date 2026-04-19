package com.arka.order.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.arka.order.domain.event.BaseEvent;
import com.arka.order.domain.event.StockFailedEvent;
import com.arka.order.domain.event.StockUpdatedEvent;
import com.arka.order.domain.model.OrderStatus;
import com.arka.order.domain.port.out.OrderRepositoryPort;
import com.arka.order.domain.port.out.ProcessedEventPort;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class StockEventConsumer {

    private static final String STOCK_UPDATED = "STOCK_UPDATED";
    private static final String STOCK_FAILED = "STOCK_FAILED";

    private final OrderRepositoryPort repository;
    private final ProcessedEventPort processedEventPort;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "stock-events", groupId = "order-group")
    public void consume(String message) {
        Mono.fromCallable(() -> objectMapper.readValue(message, BaseEvent.class))
                .flatMap(event -> processedEventPort.existsById(event.getEventId())
                        .flatMap(exists -> exists ? ignoreDuplicate(event) : processEvent(message, event)
                                .then(processedEventPort.markProcessed(event.getEventId()))))
                .doOnError(error -> log.error("Error processing stock event", error))
                .subscribe();
    }

    private Mono<Void> processEvent(String message, BaseEvent event) {
        return switch (event.getType()) {
            case STOCK_UPDATED -> handleStockUpdated(message);
            case STOCK_FAILED -> handleStockFailed(message);
            default -> {
                log.warn("Unknown stock event type={}", event.getType());
                yield Mono.empty();
            }
        };
    }

    private Mono<Void> handleStockUpdated(String message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message, StockUpdatedEvent.class))
                .flatMap(event -> updateOrderStatus(event.getOrderId(), OrderStatus.CONFIRMED)
                        .doOnSuccess(unused -> log.info("Order confirmed orderId={}", event.getOrderId())));
    }

    private Mono<Void> handleStockFailed(String message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message, StockFailedEvent.class))
                .flatMap(event -> updateOrderStatus(event.getOrderId(), OrderStatus.CANCELLED)
                        .doOnSuccess(unused -> log.warn("Order cancelled orderId={}, reason={}", event.getOrderId(),
                                event.getReason())));
    }

    private Mono<Void> updateOrderStatus(String orderId, OrderStatus status) {
        return repository.findById(Long.valueOf(orderId))
                .filter(order -> order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PROCESSING)
                .flatMap(order -> {
                    order.setStatus(status);
                    return repository.update(order);
                })
                .then();
    }

    private Mono<Void> ignoreDuplicate(BaseEvent event) {
        log.warn("Duplicated event ignored eventId={}", event.getEventId());
        return Mono.empty();
    }
}
