package com.arka.inventory.domain.port.out;

import com.arka.inventory.domain.model.StockChange;

import reactor.core.publisher.Mono;

public interface StockHistoryRepositoryPort {

    Mono<StockChange> save(StockChange stockChange);
}
