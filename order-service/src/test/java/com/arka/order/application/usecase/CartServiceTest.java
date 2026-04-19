package com.arka.order.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arka.order.domain.model.Cart;
import com.arka.order.domain.model.CartItem;
import com.arka.order.domain.model.CartStatus;
import com.arka.order.domain.port.out.CartRepositoryPort;
import com.arka.order.domain.port.out.ReminderPort;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepositoryPort cartRepository;

    @Mock
    private ReminderPort reminderPort;

    @Test
    void saveDefaultsCartStatusAndCustomer() {
        CartService service = new CartService(cartRepository, reminderPort);
        Cart cart = new Cart(null, null, null, List.of(new CartItem(1L, 2)), null, null);

        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.save(cart))
                .expectNextMatches(saved -> saved.getStatus() == CartStatus.ACTIVE
                        && saved.getCustomerId().equals("anonymous"))
                .verifyComplete();
    }

    @Test
    void listReturnsAbandonedCarts() {
        CartService service = new CartService(cartRepository, reminderPort);
        Cart cart = new Cart(1L, "client", CartStatus.ABANDONED, List.of(new CartItem(1L, 2)), null, null);

        when(cartRepository.findAbandoned()).thenReturn(Flux.just(cart));

        StepVerifier.create(service.list())
                .expectNext(cart)
                .verifyComplete();
    }

    @Test
    void sendReminderUsesReminderPort() {
        CartService service = new CartService(cartRepository, reminderPort);
        Cart cart = new Cart(1L, "client", CartStatus.ABANDONED, List.of(new CartItem(1L, 2)), null, null);

        when(cartRepository.findById(1L)).thenReturn(Mono.just(cart));
        when(reminderPort.sendCartReminder(cart)).thenReturn(Mono.empty());

        StepVerifier.create(service.sendReminder(1L))
                .verifyComplete();

        verify(reminderPort).sendCartReminder(cart);
    }
}
