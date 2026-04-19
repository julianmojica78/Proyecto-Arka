package com.arka.report.domain.port.in;

import reactor.core.publisher.Mono;

public interface WeeklySalesReportUseCase {

    Mono<String> generateCsv();
}
