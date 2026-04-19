package com.arka.order.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("outbox")
public class OutboxEventEntity {

    @Id
    @Column("event_id")
    private String eventId;

    @Column("aggregate_id")
    private String aggregateId;

    @Column("aggregate_type")
    private String aggregateType;

    @Column("event_type")
    private String eventType;

    @Column("payload")
    private String payload;

    @Column("status")
    private String status;

    @Column("correlation_id")
    private String correlationId;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("retry_count")
    private Integer retryCount;

    @Column("last_attempt")
    private LocalDateTime lastAttempt;
}