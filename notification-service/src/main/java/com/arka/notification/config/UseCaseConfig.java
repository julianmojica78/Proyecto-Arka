package com.arka.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.arka.notification.application.usecase.NotificationService;
import com.arka.notification.domain.port.in.CreateNotificationUseCase;
import com.arka.notification.domain.port.in.GetNotificationsUseCase;
import com.arka.notification.domain.port.out.NotificationRepositoryPort;

@Configuration
public class UseCaseConfig {

    @Bean
    public NotificationService notificationService(NotificationRepositoryPort repository) {
        return new NotificationService(repository);
    }

    @Bean
    public CreateNotificationUseCase createNotificationUseCase(NotificationService service) {
        return service;
    }

    @Bean
    public GetNotificationsUseCase getNotificationsUseCase(NotificationService service) {
        return service;
    }
}
