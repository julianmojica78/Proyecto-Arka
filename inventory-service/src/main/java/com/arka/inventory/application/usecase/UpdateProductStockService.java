package com.arka.inventory.application.usecase;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.arka.inventory.domain.exception.InventoryException;
import com.arka.inventory.domain.model.Product;
import com.arka.inventory.domain.model.StockChange;
import com.arka.inventory.domain.port.in.UpdateProductStockUseCase;
import com.arka.inventory.domain.port.out.ProductRepositoryPort;
import com.arka.inventory.domain.port.out.StockHistoryRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductStockService implements UpdateProductStockUseCase {

    private static final String DEFAULT_REASON = "ADMIN_UPDATE";

    private final ProductRepositoryPort productRepository;
    private final StockHistoryRepositoryPort stockHistoryRepository;

    @Override
    public Mono<Product> updateStock(Long productId, Integer newStock, String reason) {
        if (newStock == null || newStock < 0) {
            return Mono.error(new InventoryException("Error Actualizando Stock","E-10","stock no puede ser negativo","CreateProductService.validate",HttpStatus.BAD_REQUEST));
        }

        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new InventoryException("Error Actualizando Stock","E-10","El id: "+ productId +", no existe en base de datos.","CreateProductService.validate",HttpStatus.NOT_FOUND)))
                .flatMap(product -> saveWithHistory(product, newStock, reason));
    }

    private Mono<Product> saveWithHistory(Product product, Integer newStock, String reason) {
        Integer previousStock = product.getStock();
        product.setStock(newStock);

        return productRepository.save(product)
                .flatMap(saved -> stockHistoryRepository.save(new StockChange(
                        null,
                        saved.getId(),
                        previousStock,
                        newStock,
                        isBlank(reason) ? DEFAULT_REASON : reason,
                        LocalDateTime.now()))
                        .thenReturn(saved));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
