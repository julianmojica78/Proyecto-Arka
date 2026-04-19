package com.arka.report.domain.port.out;

import java.time.Instant;

import com.arka.report.domain.model.OrderView;

import reactor.core.publisher.Flux;

public interface OrderQueryPort {

    Flux<OrderView> findConfirmedOrders(Instant start, Instant end);
}
