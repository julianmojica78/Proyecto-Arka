package com.arka.inventory.infrastructure.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface StockHistoryRepository extends ReactiveCrudRepository<StockChangeEntity, Long> {
}
