package com.arka.order.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

	private String eventId;

	private String aggregateId;

	private String aggregateType;

	private String eventType;

	private String payload;

	private String status;

	private String correlationId;

	private LocalDateTime createdAt;

	private Integer retryCount;

	private LocalDateTime lastAttempt;
}
