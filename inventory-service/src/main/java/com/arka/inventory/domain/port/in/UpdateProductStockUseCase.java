package com.arka.inventory.domain.port.in;

import com.arka.inventory.domain.model.Product;

import reactor.core.publisher.Mono;

public interface UpdateProductStockUseCase {

    Mono<Product> updateStock(Long productId, Integer newStock, String reason);
}
