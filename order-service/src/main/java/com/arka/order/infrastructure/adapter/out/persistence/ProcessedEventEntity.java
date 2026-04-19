package com.arka.order.infrastructure.adapter.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("processed_events")
public class ProcessedEventEntity {

    @Id
    private String eventId;
}