package com.arka.notification.infrastructure.adapter.in.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.arka.notification.domain.port.in.CreateNotificationUseCase;
import com.arka.notification.infrastructure.adapter.in.kafka.dto.BaseEvent;
import com.arka.notification.infrastructure.adapter.in.kafka.dto.StockFailedEvent;
import com.arka.notification.infrastructure.adapter.in.kafka.dto.StockUpdatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class StockEventConsumer {

    private final CreateNotificationUseCase createNotificationUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "stock-events", groupId = "notification-group")
    public void consume(String message) {
        Mono.fromCallable(() -> objectMapper.readValue(message, BaseEvent.class))
                .flatMap(event -> switch (event.getType()) {
                    case "STOCK_UPDATED" -> handleUpdated(message);
                    case "STOCK_FAILED" -> handleFailed(message);
                    default -> Mono.empty();
                })
                .doOnError(error -> log.error("Error processing stock event", error))
                .subscribe();
    }

    private Mono<?> handleUpdated(String message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message, StockUpdatedEvent.class))
                .flatMap(event -> createNotificationUseCase.createOrderConfirmed(Long.valueOf(event.getOrderId())));
    }

    private Mono<?> handleFailed(String message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message, StockFailedEvent.class))
                .flatMap(event -> createNotificationUseCase.createOrderCancelled(
                        Long.valueOf(event.getOrderId()),
                        event.getReason()));
    }
}
