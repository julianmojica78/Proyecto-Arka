package com.arka.inventory.application.usecase;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arka.inventory.domain.exception.InventoryException;
import com.arka.inventory.domain.model.Product;
import com.arka.inventory.domain.port.out.ProductRepositoryPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreateProductServiceTest {

    @Mock
    private ProductRepositoryPort repository;

    @Test
    void createSavesValidProduct() {
        CreateProductService service = new CreateProductService(repository);
        Product product = new Product(null, "Laptop", "Core i7", BigDecimal.valueOf(2500), 5, "TECH");
        Product saved = new Product(1L, "Laptop", "Core i7", BigDecimal.valueOf(2500), 5, "TECH");

        when(repository.save(product)).thenReturn(Mono.just(saved));

        StepVerifier.create(service.create(product))
                .expectNext(saved)
                .verifyComplete();

        verify(repository).save(product);
    }

    @Test
    void createRejectsNegativeStock() {
        CreateProductService service = new CreateProductService(repository);
        Product product = new Product(null, "Laptop", "Core i7", BigDecimal.valueOf(2500), -1, "TECH");

        StepVerifier.create(service.create(product))
                .expectError(InventoryException.class)
                .verify();
    }
}
