package com.arka.order.infrastructure.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.arka.order.domain.model.OutboxEvent;
import com.arka.order.domain.model.OutboxStatus;
import com.arka.order.domain.port.out.OutboxRepositoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@ConditionalOnProperty(name = "arka.outbox.publisher.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class OutboxPublisher {

    private static final String TOPIC = "order-events";

    private final OutboxRepositoryPort outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 3000)
    public void publishEvents() {
        outboxRepository.findPendingEvents(OutboxStatus.PENDING.name())
                .flatMap(this::publishEvent)
                .doOnError(error -> log.error("Error publishing outbox events", error))
                .subscribe();
    }

    private Mono<Void> publishEvent(OutboxEvent event) {
        log.info("Publishing outbox event: {}", event.getEventId());

        return Mono.fromFuture(kafkaTemplate.send(TOPIC, event.getCorrelationId(), event.getPayload()))
                .then(outboxRepository.markAsSent(event.getEventId()))
                .doOnSuccess(unused -> log.info("Outbox event sent: {}", event.getEventId()))
                .doOnError(error -> log.error("Error sending outbox event {}", event.getEventId(), error));
    }
}
