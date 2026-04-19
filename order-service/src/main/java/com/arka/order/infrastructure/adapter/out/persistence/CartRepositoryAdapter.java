package com.arka.order.infrastructure.adapter.out.persistence;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Repository;

import com.arka.order.domain.model.Cart;
import com.arka.order.domain.model.CartItem;
import com.arka.order.domain.model.CartStatus;
import com.arka.order.domain.port.out.CartRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CartRepositoryAdapter implements CartRepositoryPort {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Mono<Cart> save(Cart cart) {
        return cartRepository.save(toEntity(cart))
                .flatMap(saved -> saveItems(saved.getId(), cart).thenReturn(saved))
                .flatMap(this::toDomainWithItems);
    }

    @Override
    public Mono<Cart> findById(Long id) {
        return cartRepository.findById(id).flatMap(this::toDomainWithItems);
    }

    @Override
    public Flux<Cart> findAbandoned() {
        return cartRepository.findByStatusOrderByUpdatedAtAsc(CartStatus.ABANDONED)
                .flatMap(this::toDomainWithItems);
    }

    private Mono<Void> saveItems(Long cartId, Cart cart) {
        return cartItemRepository.deleteByCartId(cartId)
                .thenMany(Flux.fromIterable(cart.getItems())
                        .map(item -> {
                            CartItemEntity entity = new CartItemEntity();
                            entity.setCartId(cartId);
                            entity.setProductId(item.getProductId());
                            entity.setQuantity(item.getQuantity());
                            return entity;
                        }))
                .flatMap(cartItemRepository::save)
                .then();
    }

    private Mono<Cart> toDomainWithItems(CartEntity entity) {
        return cartItemRepository.findByCartId(entity.getId())
                .map(item -> new CartItem(item.getProductId(), item.getQuantity()))
                .collectList()
                .map(items -> new Cart(
                        entity.getId(),
                        entity.getCustomerId(),
                        entity.getStatus(),
                        items,
                        toInstant(entity.getCreatedAt()),
                        toInstant(entity.getUpdatedAt())));
    }

    private CartEntity toEntity(Cart cart) {
        CartEntity entity = new CartEntity();
        entity.setId(cart.getId());
        entity.setCustomerId(cart.getCustomerId());
        entity.setStatus(cart.getStatus());
        entity.setCreatedAt(cart.getCreatedAt() != null ? toLocalDateTime(cart.getCreatedAt()) : LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    private Instant toInstant(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.atZone(ZoneId.systemDefault()).toInstant() : null;
    }
}
