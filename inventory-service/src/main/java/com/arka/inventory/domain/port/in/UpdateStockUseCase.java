package com.arka.inventory.domain.port.in;

import java.util.List;

import com.arka.inventory.domain.model.StockItem;

import reactor.core.publisher.Mono;

public interface UpdateStockUseCase {
	Mono<Void> updateStock(List<StockItem> items);
	
	Mono<Void> restoreStock(List<StockItem> items);
}
