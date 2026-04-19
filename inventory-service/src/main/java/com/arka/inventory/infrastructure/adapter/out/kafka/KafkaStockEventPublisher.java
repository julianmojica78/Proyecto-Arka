package com.arka.inventory.infrastructure.adapter.out.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.arka.inventory.domain.event.StockFailedEvent;
import com.arka.inventory.domain.event.StockUpdatedEvent;
import com.arka.inventory.domain.port.out.StockEventPublisherPort;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class KafkaStockEventPublisher implements StockEventPublisherPort {

    private static final String TOPIC = "stock-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> publishStockUpdated(StockUpdatedEvent event) {
        return publish(event.getCorrelationId(), event);
    }

    @Override
    public Mono<Void> publishStockFailed(StockFailedEvent event) {
        return publish(event.getCorrelationId(), event);
    }

    private Mono<Void> publish(String key, Object event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .flatMap(payload -> Mono.fromFuture(kafkaTemplate.send(TOPIC, key, payload)))
                .then();
    }
}
