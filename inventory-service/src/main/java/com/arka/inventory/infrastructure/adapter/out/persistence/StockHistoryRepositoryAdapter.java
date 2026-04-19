package com.arka.inventory.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Repository;

import com.arka.inventory.domain.model.StockChange;
import com.arka.inventory.domain.port.out.StockHistoryRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class StockHistoryRepositoryAdapter implements StockHistoryRepositoryPort {

    private final StockHistoryRepository repository;

    @Override
    public Mono<StockChange> save(StockChange stockChange) {
        return repository.save(toEntity(stockChange)).map(this::toDomain);
    }

    private StockChangeEntity toEntity(StockChange stockChange) {
        StockChangeEntity entity = new StockChangeEntity();
        entity.setId(stockChange.getId());
        entity.setProductId(stockChange.getProductId());
        entity.setPreviousStock(stockChange.getPreviousStock());
        entity.setNewStock(stockChange.getNewStock());
        entity.setReason(stockChange.getReason());
        entity.setChangedAt(stockChange.getChangedAt());
        return entity;
    }

    private StockChange toDomain(StockChangeEntity entity) {
        return new StockChange(
                entity.getId(),
                entity.getProductId(),
                entity.getPreviousStock(),
                entity.getNewStock(),
                entity.getReason(),
                entity.getChangedAt());
    }
}
