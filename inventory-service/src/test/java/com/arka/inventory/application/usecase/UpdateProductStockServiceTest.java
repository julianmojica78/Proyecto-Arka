package com.arka.inventory.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arka.inventory.domain.exception.InventoryException;
import com.arka.inventory.domain.model.Product;
import com.arka.inventory.domain.model.StockChange;
import com.arka.inventory.domain.port.out.ProductRepositoryPort;
import com.arka.inventory.domain.port.out.StockHistoryRepositoryPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UpdateProductStockServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @Mock
    private StockHistoryRepositoryPort stockHistoryRepository;

    @Test
    void updateStockSavesProductAndHistory() {
        UpdateProductStockService service = new UpdateProductStockService(productRepository, stockHistoryRepository);
        Product product = new Product(1L, "Mouse", "Wireless", BigDecimal.TEN, 3, "TECH");

        when(productRepository.findById(1L)).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(stockHistoryRepository.save(any(StockChange.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.updateStock(1L, 12, "RESTOCK"))
                .expectNextMatches(saved -> saved.getStock() == 12)
                .verifyComplete();

        verify(stockHistoryRepository).save(any(StockChange.class));
    }

    @Test
    void updateStockRejectsNegativeValue() {
        UpdateProductStockService service = new UpdateProductStockService(productRepository, stockHistoryRepository);

        StepVerifier.create(service.updateStock(1L, -2, "BAD"))
                .expectError(InventoryException.class)
                .verify();
    }
}
