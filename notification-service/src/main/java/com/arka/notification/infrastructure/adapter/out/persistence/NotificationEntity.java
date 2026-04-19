package com.arka.notification.infrastructure.adapter.out.persistence;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.arka.notification.domain.model.NotificationType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("notifications")
public class NotificationEntity {

    @Id
    private Long id;

    @Column("order_id")
    private Long orderId;

    @Column("cart_id")
    private Long cartId;

    @Column("customer_id")
    private String customerId;

    private NotificationType type;
    private String message;

    @Column("created_at")
    private LocalDateTime createdAt;
}
