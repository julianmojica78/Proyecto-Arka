package com.arka.inventory.infrastructure.adapter.in.web;

import org.springframework.stereotype.Component;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.arka.inventory.domain.model.Product;
import com.arka.inventory.domain.port.in.CreateProductUseCase;
import com.arka.inventory.domain.port.in.GetProductsUseCase;
import com.arka.inventory.domain.port.in.UpdateProductStockUseCase;
import com.arka.inventory.infrastructure.adapter.in.web.dto.ProductRequest;
import com.arka.inventory.infrastructure.adapter.in.web.dto.StockUpdateRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final CreateProductUseCase createUseCase;
    private final GetProductsUseCase getUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(ProductRequest.class)
                .map(req -> new Product(
                        null,
                        req.getName(),
                        req.getDescription(),
                        req.getPrice(),
                        req.getStock(),
                        req.getCategory()
                ))
                .flatMap(createUseCase::create)
                .flatMap(p -> ServerResponse.ok().bodyValue(p));
    }

    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok().body(getUseCase.getAll(), Product.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ServerResponse> updateStock(ServerRequest request) {
        Long productId = Long.valueOf(request.pathVariable("id"));

        return request.bodyToMono(StockUpdateRequest.class)
                .flatMap(body -> updateProductStockUseCase.updateStock(productId, body.getStock(), body.getReason()))
                .flatMap(product -> ServerResponse.ok().bodyValue(product));
    }

}
