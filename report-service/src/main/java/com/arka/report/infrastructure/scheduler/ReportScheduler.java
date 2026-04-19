package com.arka.report.infrastructure.scheduler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.arka.report.domain.port.in.LowStockReportUseCase;
import com.arka.report.domain.port.in.WeeklySalesReportUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arka.reports.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class ReportScheduler {

    private final LowStockReportUseCase lowStockReportUseCase;
    private final WeeklySalesReportUseCase weeklySalesReportUseCase;

    @Scheduled(cron = "${arka.reports.low-stock.cron:0 0 8 * * MON}")
    public void lowStock() {
        lowStockReportUseCase.generateCsv(10)
                .doOnNext(csv -> log.info("Low-stock report generated with {} characters", csv.length()))
                .subscribe();
    }

    @Scheduled(cron = "${arka.reports.sales.cron:0 5 8 * * MON}")
    public void sales() {
        weeklySalesReportUseCase.generateCsv()
                .doOnNext(csv -> log.info("Weekly sales report generated with {} characters", csv.length()))
                .subscribe();
    }
}
