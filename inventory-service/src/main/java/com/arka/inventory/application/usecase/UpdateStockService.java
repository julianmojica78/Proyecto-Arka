package com.arka.inventory.application.usecase;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.arka.inventory.domain.exception.InventoryException;
import com.arka.inventory.domain.model.Product;
import com.arka.inventory.domain.model.StockItem;
import com.arka.inventory.domain.port.in.UpdateStockUseCase;
import com.arka.inventory.domain.port.out.ProductRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateStockService implements UpdateStockUseCase {

    private final ProductRepositoryPort repository;

    @Override
    public Mono<Void> updateStock(List<StockItem> items) {
        return Flux.fromIterable(items)
                .concatMap(item -> repository.findById(item.getProductId())
                        .switchIfEmpty(Mono.error(new InventoryException("Producto no Encuentrado.", "E-10", "El Producto con el Id: " + item.getProductId() + ", No se Encuentra Registrado para Actualizar el Stock.", "UpdateStockService.updateStock", HttpStatus.NOT_FOUND)))
                        .flatMap(product -> reserve(product, item)))
                .then();
    }

    @Override
    public Mono<Void> restoreStock(List<StockItem> items) {
        return Flux.fromIterable(items)
                .concatMap(item -> repository.findById(item.getProductId())
                        .switchIfEmpty(Mono.error(new InventoryException("Producto no Encuentrado.", "E-10", "El Producto con el Id: " + item.getProductId() + ", No se Encuentra Registrado para Restablecer el Stock.", "UpdateStockService.restoreStock", HttpStatus.NOT_FOUND)))
                        .flatMap(product -> restore(product, item)))
                .then();
    }

    private Mono<Product> reserve(Product product, StockItem item) {
        if (product.getStock() < item.getQuantity()) {
            return Mono.error(new InventoryException("Stock Insuficiente..", "E-10", "El Producto: " + product.getName() + ", No Cuenta con Stock Suficiente, para reservar..", "UpdateStockService.reserve", HttpStatus.NOT_FOUND));
        }

        product.setStock(product.getStock() - item.getQuantity());
        return repository.save(product);
    }

    private Mono<Product> restore(Product product, StockItem item) {
        product.setStock(product.getStock() + item.getQuantity());
        return repository.save(product);
    }
}
