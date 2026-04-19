package com.arka.report.domain.port.in;

import reactor.core.publisher.Mono;

public interface LowStockReportUseCase {

    Mono<String> generateCsv(Integer threshold);
}
