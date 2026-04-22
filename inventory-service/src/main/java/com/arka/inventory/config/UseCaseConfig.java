package com.arka.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.arka.inventory.application.usecase.CreateProductService;
import com.arka.inventory.application.usecase.GetProductsService;
import com.arka.inventory.application.usecase.StockChangeRecorder;
import com.arka.inventory.application.usecase.UpdateProductStockService;
import com.arka.inventory.application.usecase.UpdateStockService;
import com.arka.inventory.domain.port.in.CreateProductUseCase;
import com.arka.inventory.domain.port.in.GetProductsUseCase;
import com.arka.inventory.domain.port.in.UpdateProductStockUseCase;
import com.arka.inventory.domain.port.in.UpdateStockUseCase;
import com.arka.inventory.domain.port.out.ProductRepositoryPort;
import com.arka.inventory.domain.port.out.StockHistoryRepositoryPort;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateProductUseCase createProductUseCase(ProductRepositoryPort repository) {
        return new CreateProductService(repository);
    }

    @Bean
    public GetProductsUseCase getProductsUseCase(ProductRepositoryPort repository) {
        return new GetProductsService(repository);
    }

    @Bean
    public StockChangeRecorder stockChangeRecorder(
            ProductRepositoryPort repository,
            StockHistoryRepositoryPort stockHistoryRepository) {
        return new StockChangeRecorder(repository, stockHistoryRepository);
    }

    @Bean
    public UpdateStockUseCase updateStockUseCase(
            ProductRepositoryPort repository,
            StockChangeRecorder stockChangeRecorder) {
        return new UpdateStockService(repository, stockChangeRecorder);
    }

    @Bean
    public UpdateProductStockUseCase updateProductStockUseCase(
            ProductRepositoryPort repository,
            StockChangeRecorder stockChangeRecorder) {
        return new UpdateProductStockService(repository, stockChangeRecorder);
    }
}
