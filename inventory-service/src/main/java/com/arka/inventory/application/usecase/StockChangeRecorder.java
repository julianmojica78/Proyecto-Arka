package com.arka.inventory.application.usecase;

import java.time.LocalDateTime;

import com.arka.inventory.domain.model.Product;
import com.arka.inventory.domain.model.StockChange;
import com.arka.inventory.domain.model.StockChangeReason;
import com.arka.inventory.domain.port.out.ProductRepositoryPort;
import com.arka.inventory.domain.port.out.StockHistoryRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class StockChangeRecorder {

    private final ProductRepositoryPort productRepository;
    private final StockHistoryRepositoryPort stockHistoryRepository;

    public Mono<Product> record(Product product, Integer newStock, StockChangeReason reason) {
        return record(product, newStock, reason.name());
    }

    public Mono<Product> record(Product product, Integer newStock, String reason) {
        Integer previousStock = product.getStock();
        product.setStock(newStock);

        return productRepository.save(product)
                .flatMap(saved -> stockHistoryRepository.save(new StockChange(
                        null,
                        saved.getId(),
                        previousStock,
                        saved.getStock(),
                        normalizeReason(reason),
                        LocalDateTime.now()))
                        .thenReturn(saved));
    }

    private String normalizeReason(String reason) {
        return reason == null || reason.isBlank()
                ? StockChangeReason.MANUAL_UPDATE.name()
                : reason;
    }
}
