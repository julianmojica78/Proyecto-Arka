package com.arka.inventory.application.usecase;

import com.arka.inventory.domain.model.Product;
import com.arka.inventory.domain.port.in.GetProductsUseCase;
import com.arka.inventory.domain.port.out.ProductRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetProductsService implements GetProductsUseCase {

    private final ProductRepositoryPort repository;

    @Override
    public Flux<Product> getAll() {
        return repository.findAll();
    }
}
