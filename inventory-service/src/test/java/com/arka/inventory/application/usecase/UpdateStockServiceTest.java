package com.arka.inventory.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arka.inventory.domain.exception.InventoryException;
import com.arka.inventory.domain.model.Product;
import com.arka.inventory.domain.model.StockChange;
import com.arka.inventory.domain.model.StockChangeReason;
import com.arka.inventory.domain.model.StockItem;
import com.arka.inventory.domain.port.out.ProductRepositoryPort;
import com.arka.inventory.domain.port.out.StockHistoryRepositoryPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UpdateStockServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @Mock
    private StockHistoryRepositoryPort stockHistoryRepository;

    @Test
    void updateStockReservesStockAndSavesHistory() {
        UpdateStockService service = service();
        Product product = new Product(1L, "Mouse", "Wireless", BigDecimal.TEN, 10, "TECH");

        when(productRepository.findById(1L)).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(stockHistoryRepository.save(any(StockChange.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.updateStock(List.of(new StockItem(1L, 4))))
                .verifyComplete();

        ArgumentCaptor<StockChange> captor = ArgumentCaptor.forClass(StockChange.class);
        verify(stockHistoryRepository).save(captor.capture());

        StockChange stockChange = captor.getValue();
        assertEquals(1L, stockChange.getProductId());
        assertEquals(10, stockChange.getPreviousStock());
        assertEquals(6, stockChange.getNewStock());
        assertEquals(StockChangeReason.ORDER_RESERVED.name(), stockChange.getReason());
    }

    @Test
    void restoreStockRestoresStockAndSavesHistory() {
        UpdateStockService service = service();
        Product product = new Product(1L, "Mouse", "Wireless", BigDecimal.TEN, 10, "TECH");

        when(productRepository.findById(1L)).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(stockHistoryRepository.save(any(StockChange.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.restoreStock(List.of(new StockItem(1L, 3))))
                .verifyComplete();

        ArgumentCaptor<StockChange> captor = ArgumentCaptor.forClass(StockChange.class);
        verify(stockHistoryRepository).save(captor.capture());

        StockChange stockChange = captor.getValue();
        assertEquals(1L, stockChange.getProductId());
        assertEquals(10, stockChange.getPreviousStock());
        assertEquals(13, stockChange.getNewStock());
        assertEquals(StockChangeReason.STOCK_RESTORED.name(), stockChange.getReason());
    }

    @Test
    void updateStockRejectsInsufficientStockWithoutSavingHistory() {
        UpdateStockService service = service();
        Product product = new Product(1L, "Mouse", "Wireless", BigDecimal.TEN, 2, "TECH");

        when(productRepository.findById(1L)).thenReturn(Mono.just(product));

        StepVerifier.create(service.updateStock(List.of(new StockItem(1L, 4))))
                .expectError(InventoryException.class)
                .verify();

        verify(productRepository, never()).save(any(Product.class));
        verify(stockHistoryRepository, never()).save(any(StockChange.class));
    }

    private UpdateStockService service() {
        StockChangeRecorder stockChangeRecorder = new StockChangeRecorder(productRepository, stockHistoryRepository);
        return new UpdateStockService(productRepository, stockChangeRecorder);
    }
}
