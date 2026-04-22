package com.arka.inventory.application.usecase;

import org.springframework.http.HttpStatus;

import com.arka.inventory.domain.exception.InventoryException;
import com.arka.inventory.domain.model.Product;
import com.arka.inventory.domain.model.StockChangeReason;
import com.arka.inventory.domain.port.in.UpdateProductStockUseCase;
import com.arka.inventory.domain.port.out.ProductRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductStockService implements UpdateProductStockUseCase {

    private final ProductRepositoryPort productRepository;
    private final StockChangeRecorder stockChangeRecorder;

    @Override
    public Mono<Product> updateStock(Long productId, Integer newStock, String reason) {
        if (newStock == null || newStock < 0) {
            return Mono.error(new InventoryException("Error Actualizando Stock","E-10","stock no puede ser negativo","CreateProductService.validate",HttpStatus.BAD_REQUEST));
        }

        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new InventoryException("Error Actualizando Stock","E-10","El id: "+ productId +", no existe en base de datos.","CreateProductService.validate",HttpStatus.NOT_FOUND)))
                .flatMap(product -> stockChangeRecorder.record(product, newStock, resolveReason(reason)));
    }

    private String resolveReason(String reason) {
        return reason == null || reason.isBlank()
                ? StockChangeReason.MANUAL_UPDATE.name()
                : reason;
    }
}
