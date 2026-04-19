package com.arka.notification.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arka.notification.domain.model.CartReminderCommand;
import com.arka.notification.domain.model.CartReminderItem;
import com.arka.notification.domain.model.NotificationRecord;
import com.arka.notification.domain.model.NotificationType;
import com.arka.notification.domain.port.out.NotificationRepositoryPort;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepositoryPort repository;

    @Test
    void createOrderConfirmedStoresNotification() {
        NotificationService service = new NotificationService(repository);

        when(repository.save(any(NotificationRecord.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.createOrderConfirmed(10L))
                .assertNext(notification -> {
                    assertThat(notification.getOrderId()).isEqualTo(10L);
                    assertThat(notification.getType()).isEqualTo(NotificationType.ORDER_CONFIRMED);
                })
                .verifyComplete();
    }

    @Test
    void findByOrderIdReturnsNotifications() {
        NotificationService service = new NotificationService(repository);
        NotificationRecord record = new NotificationRecord(1L, 10L, null, null, NotificationType.ORDER_CONFIRMED,
                "Tu pedido fue confirmado", null);

        when(repository.findByOrderId(10L)).thenReturn(Flux.just(record));

        StepVerifier.create(service.findByOrderId(10L))
                .expectNext(record)
                .verifyComplete();
    }

    @Test
    void createCartReminderBuildsReadableMessage() {
        NotificationService service = new NotificationService(repository);
        CartReminderCommand command = new CartReminderCommand(5L, "client-1", List.of(new CartReminderItem(9L, 2)));

        when(repository.save(any(NotificationRecord.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.createCartReminder(command))
                .assertNext(notification -> assertThat(notification.getMessage()).contains("9x2"))
                .verifyComplete();
    }
}
