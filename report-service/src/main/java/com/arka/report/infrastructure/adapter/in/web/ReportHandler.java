package com.arka.report.infrastructure.adapter.in.web;

import com.arka.report.domain.port.in.LowStockReportUseCase;
import com.arka.report.domain.port.in.WeeklySalesReportUseCase;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReportHandler {

    private final LowStockReportUseCase lowStockReportUseCase;
    private final WeeklySalesReportUseCase weeklySalesReportUseCase;

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ServerResponse> lowStock(ServerRequest request) {
        Integer threshold = request.queryParam("threshold").map(Integer::valueOf).orElse(null);
        return lowStockReportUseCase.generateCsv(threshold)
                .flatMap(csv -> ServerResponse.ok()
                        .header("Content-Type", "text/csv")
                        .header("Content-Disposition", "attachment; filename=low-stock-report.csv")
                        .bodyValue(csv));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ServerResponse> weeklySales(ServerRequest request) {
        return weeklySalesReportUseCase.generateCsv()
                .flatMap(csv -> ServerResponse.ok()
                        .header("Content-Type", "text/csv")
                        .header("Content-Disposition", "attachment; filename=weekly-sales-report.csv")
                        .bodyValue(csv));
    }
}
