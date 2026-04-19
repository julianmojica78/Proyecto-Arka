package com.arka.order.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arka.order.domain.model.CreateOrderCommand;
import com.arka.order.domain.model.Order;
import com.arka.order.domain.model.OrderItem;
import com.arka.order.domain.model.OrderStatus;
import com.arka.order.domain.model.OutboxEvent;
import com.arka.order.domain.port.out.EventSerializerPort;
import com.arka.order.domain.port.out.OrderRepositoryPort;
import com.arka.order.domain.port.out.OutboxRepositoryPort;
import com.arka.order.domain.port.out.TransactionPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private OutboxRepositoryPort outboxRepository;

    @Mock
    private EventSerializerPort eventSerializer;

    @Mock
    private TransactionPort transactionPort;

    @Test
    void createSavesPendingOrderAndOutboxEvent() {
        CreateOrderService service = new CreateOrderService(
                orderRepository,
                outboxRepository,
                eventSerializer,
                transactionPort);

        when(transactionPort.transactional(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(10L);
            return Mono.just(order);
        });
        when(eventSerializer.serialize(any())).thenReturn("{event}");
        when(outboxRepository.save(any(OutboxEvent.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        CreateOrderCommand command = new CreateOrderCommand("client-1", List.of(new OrderItem(1L, 2)));

        StepVerifier.create(service.create(command))
                .expectNextMatches(order -> order.getId().equals(10L)
                        && order.getCustomerId().equals("client-1")
                        && order.getStatus() == OrderStatus.PENDING)
                .verifyComplete();

        verify(outboxRepository).save(any(OutboxEvent.class));
    }
}
