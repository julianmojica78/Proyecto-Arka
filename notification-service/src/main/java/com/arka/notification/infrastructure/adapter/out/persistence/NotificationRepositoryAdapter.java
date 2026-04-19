package com.arka.notification.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Repository;

import com.arka.notification.domain.model.NotificationRecord;
import com.arka.notification.domain.port.out.NotificationRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final NotificationRepository repository;

    @Override
    public Mono<NotificationRecord> save(NotificationRecord notification) {
        return repository.save(toEntity(notification)).map(this::toDomain);
    }

    @Override
    public Flux<NotificationRecord> findByOrderId(Long orderId) {
        return repository.findByOrderIdOrderByCreatedAtDesc(orderId).map(this::toDomain);
    }

    private NotificationEntity toEntity(NotificationRecord notification) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(notification.getId());
        entity.setOrderId(notification.getOrderId());
        entity.setCartId(notification.getCartId());
        entity.setCustomerId(notification.getCustomerId());
        entity.setType(notification.getType());
        entity.setMessage(notification.getMessage());
        entity.setCreatedAt(notification.getCreatedAt());
        return entity;
    }

    private NotificationRecord toDomain(NotificationEntity entity) {
        return new NotificationRecord(
                entity.getId(),
                entity.getOrderId(),
                entity.getCartId(),
                entity.getCustomerId(),
                entity.getType(),
                entity.getMessage(),
                entity.getCreatedAt());
    }
}
