package com.arka.inventory.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Repository;

import com.arka.inventory.domain.model.Product;
import com.arka.inventory.domain.port.out.ProductRepositoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductRepository repository;

    @Override
    public Mono<Product> save(Product product) {
        return repository.save(toEntity(product))
                .doOnSuccess(e -> log.info("Product persisted id={}, stock={}", e.getId(), e.getStock()))
                .map(this::toDomain);
    }

    @Override
    public Flux<Product> findAll() {
        return repository.findAll().map(this::toDomain);
    }

    @Override
    public Mono<Product> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<Product> findByStockLessThan(Integer threshold) {
        return repository.findByStockLessThan(threshold).map(this::toDomain);
    }

    private ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setStock(product.getStock());
        entity.setCategory(product.getCategory());
        return entity;
    }

    private Product toDomain(ProductEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStock(),
                entity.getCategory());
    }
}
