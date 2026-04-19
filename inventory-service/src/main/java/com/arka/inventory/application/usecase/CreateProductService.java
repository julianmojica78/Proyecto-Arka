package com.arka.inventory.application.usecase;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;

import com.arka.inventory.domain.exception.InventoryException;
import com.arka.inventory.domain.model.Product;
import com.arka.inventory.domain.port.in.CreateProductUseCase;
import com.arka.inventory.domain.port.out.ProductRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateProductService implements CreateProductUseCase {

    private final ProductRepositoryPort repository;

    @Override
    public Mono<Product> create(Product product) {
        return validate(product).flatMap(repository::save);
    }

    private Mono<Product> validate(Product product) {
        if (isBlank(product.getName())) {
            return Mono.error(new InventoryException("Error Creando Productos","E-10","name es obligatorio","CreateProductService.validate",HttpStatus.NOT_FOUND));
        }
        if (isBlank(product.getDescription())) {
            return Mono.error(new InventoryException("Error Creando Productos","E-10","description es obligatorio","CreateProductService.validate",HttpStatus.NOT_FOUND));
        }
        if (isBlank(product.getCategory())) {
            return Mono.error(new InventoryException("Error Creando Productos","E-10","category es obligatorio","CreateProductService.validate",HttpStatus.NOT_FOUND));
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new InventoryException("Error Creando Productos","E-10","price debe ser mayor a cero","CreateProductService.validate",HttpStatus.BAD_REQUEST));
        }
        if (product.getStock() == null || product.getStock() < 0) {
            return Mono.error(new InventoryException("Error Creando Productos","E-10","stock no puede ser negativo","CreateProductService.validate",HttpStatus.BAD_REQUEST));
        }
        return Mono.just(product);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
