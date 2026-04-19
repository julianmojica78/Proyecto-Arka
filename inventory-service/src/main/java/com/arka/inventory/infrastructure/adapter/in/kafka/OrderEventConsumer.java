package com.arka.inventory.infrastructure.adapter.in.kafka;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.arka.inventory.domain.event.StockFailedEvent;
import com.arka.inventory.domain.event.StockUpdatedEvent;
import com.arka.inventory.domain.model.StockItem;
import com.arka.inventory.domain.port.in.UpdateStockUseCase;
import com.arka.inventory.domain.port.out.StockEventPublisherPort;
import com.arka.inventory.infrastructure.adapter.in.kafka.dto.OrderCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private static final String STOCK_UPDATED = "STOCK_UPDATED";
    private static final String STOCK_FAILED = "STOCK_FAILED";
    private static final String EVENT_VERSION = "v1";

    private final UpdateStockUseCase updateStockUseCase;
    private final StockEventPublisherPort stockEventPublisher;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events", groupId = "inventory-group")
    public void consume(String message) {
        Mono.fromCallable(() -> objectMapper.readValue(message, OrderCreatedEvent.class))
                .flatMap(event -> updateStockUseCase.updateStock(toStockItems(event))
                        .then(publishSuccess(event))
                        .onErrorResume(error -> publishFailure(event, error.getMessage())))
                .doOnError(error -> log.error("Error processing order event", error))
                .subscribe();
    }

    private List<StockItem> toStockItems(OrderCreatedEvent event) {
        return event.getItems().stream()
                .map(item -> new StockItem(item.getProductId(), item.getQuantity()))
                .toList();
    }

    private Mono<Void> publishSuccess(OrderCreatedEvent event) {
        StockUpdatedEvent success = new StockUpdatedEvent(
                UUID.randomUUID().toString(),
                event.getCorrelationId(),
                STOCK_UPDATED,
                Instant.now(),
                event.getEventVersion() != null ? event.getEventVersion() : EVENT_VERSION,
                event.getOrderId());

        return stockEventPublisher.publishStockUpdated(success)
                .doOnSuccess(unused -> log.info("Stock updated orderId={}", event.getOrderId()));
    }

    private Mono<Void> publishFailure(OrderCreatedEvent event, String reason) {
        StockFailedEvent failure = new StockFailedEvent(
                UUID.randomUUID().toString(),
                event.getCorrelationId(),
                STOCK_FAILED,
                Instant.now(),
                event.getEventVersion() != null ? event.getEventVersion() : EVENT_VERSION,
                event.getOrderId(),
                reason);

        return stockEventPublisher.publishStockFailed(failure)
                .doOnSuccess(unused -> log.warn("Stock failed orderId={}, reason={}", event.getOrderId(), reason));
    }
}
