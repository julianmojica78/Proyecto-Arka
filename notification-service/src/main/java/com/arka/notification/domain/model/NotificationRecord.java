package com.arka.notification.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRecord {

    private Long id;
    private Long orderId;
    private Long cartId;
    private String customerId;
    private NotificationType type;
    private String message;
    private LocalDateTime createdAt;
}
