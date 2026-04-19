package com.arka.report.domain.port.out;

import com.arka.report.domain.model.ProductView;

import reactor.core.publisher.Flux;

public interface InventoryQueryPort {

    Flux<ProductView> findAllProducts();
}
