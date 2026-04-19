package com.arka.order.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arka.order.domain.exception.OrderException;
import com.arka.order.domain.model.ModifyOrderCommand;
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
class ModifyOrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private OutboxRepositoryPort outboxRepository;

    @Mock
    private EventSerializerPort eventSerializer;

    @Mock
    private TransactionPort transactionPort;

    @Test
    void modifyUpdatesPendingOrderAndPendingOutbox() {
        ModifyOrderService service = new ModifyOrderService(
                orderRepository,
                outboxRepository,
                eventSerializer,
                transactionPort);
        Order order = new Order();
        order.setId(10L);
        order.setStatus(OrderStatus.PENDING);
        order.setCorrelationId("corr");

        when(transactionPort.transactional(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.findById(10L)).thenReturn(Mono.just(order));
        when(orderRepository.update(any(Order.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(outboxRepository.findPendingByAggregateIdAndEventType("10", "ORDER_CREATED", "PENDING"))
                .thenReturn(Mono.just(new OutboxEvent()));
        when(eventSerializer.serialize(any())).thenReturn("{updated}");
        when(outboxRepository.save(any(OutboxEvent.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        ModifyOrderCommand command = new ModifyOrderCommand(10L, List.of(new OrderItem(2L, 3)));

        StepVerifier.create(service.modify(command))
                .expectNextMatches(updated -> updated.getItems().get(0).getProductId().equals(2L))
                .verifyComplete();

        verify(outboxRepository).save(any(OutboxEvent.class));
    }

    @Test
    void modifyRejectsConfirmedOrder() {
        ModifyOrderService service = new ModifyOrderService(
                orderRepository,
                outboxRepository,
                eventSerializer,
                transactionPort);
        Order order = new Order();
        order.setId(10L);
        order.setStatus(OrderStatus.CONFIRMED);

        when(transactionPort.transactional(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.findById(10L)).thenReturn(Mono.just(order));

        StepVerifier.create(service.modify(new ModifyOrderCommand(10L, List.of(new OrderItem(2L, 3)))))
                .expectError(OrderException.class)
                .verify();
    }
}
