package com.arka.inventory.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arka.inventory.domain.exception.InventoryException;
import com.arka.inventory.domain.model.Product;
import com.arka.inventory.domain.model.StockChange;
import com.arka.inventory.domain.model.StockChangeReason;
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
        StockChangeRecorder stockChangeRecorder = new StockChangeRecorder(productRepository, stockHistoryRepository);
        UpdateProductStockService service = new UpdateProductStockService(productRepository, stockChangeRecorder);
        Product product = new Product(1L, "Mouse", "Wireless", BigDecimal.TEN, 3, "TECH");

        when(productRepository.findById(1L)).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(stockHistoryRepository.save(any(StockChange.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.updateStock(1L, 12, "RESTOCK"))
                .expectNextMatches(saved -> saved.getStock() == 12)
                .verifyComplete();

        ArgumentCaptor<StockChange> captor = ArgumentCaptor.forClass(StockChange.class);
        verify(stockHistoryRepository).save(captor.capture());

        StockChange stockChange = captor.getValue();
        assertEquals(1L, stockChange.getProductId());
        assertEquals(3, stockChange.getPreviousStock());
        assertEquals(12, stockChange.getNewStock());
        assertEquals("RESTOCK", stockChange.getReason());
    }

    @Test
    void updateStockUsesManualReasonWhenReasonIsBlank() {
        StockChangeRecorder stockChangeRecorder = new StockChangeRecorder(productRepository, stockHistoryRepository);
        UpdateProductStockService service = new UpdateProductStockService(productRepository, stockChangeRecorder);
        Product product = new Product(1L, "Mouse", "Wireless", BigDecimal.TEN, 3, "TECH");

        when(productRepository.findById(1L)).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(stockHistoryRepository.save(any(StockChange.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.updateStock(1L, 12, " "))
                .expectNextMatches(saved -> saved.getStock() == 12)
                .verifyComplete();

        ArgumentCaptor<StockChange> captor = ArgumentCaptor.forClass(StockChange.class);
        verify(stockHistoryRepository).save(captor.capture());

        assertEquals(StockChangeReason.MANUAL_UPDATE.name(), captor.getValue().getReason());
    }

    @Test
    void updateStockRejectsNegativeValue() {
        StockChangeRecorder stockChangeRecorder = new StockChangeRecorder(productRepository, stockHistoryRepository);
        UpdateProductStockService service = new UpdateProductStockService(productRepository, stockChangeRecorder);

        StepVerifier.create(service.updateStock(1L, -2, "BAD"))
                .expectError(InventoryException.class)
                .verify();
    }
}
