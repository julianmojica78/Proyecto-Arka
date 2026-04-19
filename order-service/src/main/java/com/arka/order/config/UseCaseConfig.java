package com.arka.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.arka.order.application.usecase.CartService;
import com.arka.order.application.usecase.CreateOrderService;
import com.arka.order.application.usecase.ListConfirmedOrdersService;
import com.arka.order.application.usecase.ModifyOrderService;
import com.arka.order.domain.port.in.CreateOrderUseCase;
import com.arka.order.domain.port.in.ListAbandonedCartsUseCase;
import com.arka.order.domain.port.in.ListConfirmedOrdersUseCase;
import com.arka.order.domain.port.in.ModifyOrderUseCase;
import com.arka.order.domain.port.in.SaveCartUseCase;
import com.arka.order.domain.port.in.SendCartReminderUseCase;
import com.arka.order.domain.port.out.CartRepositoryPort;
import com.arka.order.domain.port.out.EventSerializerPort;
import com.arka.order.domain.port.out.OrderRepositoryPort;
import com.arka.order.domain.port.out.OutboxRepositoryPort;
import com.arka.order.domain.port.out.ReminderPort;
import com.arka.order.domain.port.out.TransactionPort;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateOrderUseCase createOrderUseCase(
            OrderRepositoryPort orderRepository,
            OutboxRepositoryPort outboxRepository,
            EventSerializerPort eventSerializer,
            TransactionPort transactionPort) {
        return new CreateOrderService(orderRepository, outboxRepository, eventSerializer, transactionPort);
    }

    @Bean
    public ModifyOrderUseCase modifyOrderUseCase(
            OrderRepositoryPort orderRepository,
            OutboxRepositoryPort outboxRepository,
            EventSerializerPort eventSerializer,
            TransactionPort transactionPort) {
        return new ModifyOrderService(orderRepository, outboxRepository, eventSerializer, transactionPort);
    }

    @Bean
    public ListConfirmedOrdersUseCase listConfirmedOrdersUseCase(OrderRepositoryPort orderRepository) {
        return new ListConfirmedOrdersService(orderRepository);
    }

    @Bean
    public CartService cartService(CartRepositoryPort cartRepository, ReminderPort reminderPort) {
        return new CartService(cartRepository, reminderPort);
    }

    @Bean
    public SaveCartUseCase saveCartUseCase(CartService cartService) {
        return cartService;
    }

    @Bean
    public ListAbandonedCartsUseCase listAbandonedCartsUseCase(CartService cartService) {
        return cartService;
    }

    @Bean
    public SendCartReminderUseCase sendCartReminderUseCase(CartService cartService) {
        return cartService;
    }
}
