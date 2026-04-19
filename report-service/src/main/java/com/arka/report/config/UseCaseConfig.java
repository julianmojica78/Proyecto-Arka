package com.arka.report.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.arka.report.application.usecase.LowStockReportService;
import com.arka.report.application.usecase.WeeklySalesReportService;
import com.arka.report.domain.port.in.LowStockReportUseCase;
import com.arka.report.domain.port.in.WeeklySalesReportUseCase;
import com.arka.report.domain.port.out.InventoryQueryPort;
import com.arka.report.domain.port.out.OrderQueryPort;

@Configuration
public class UseCaseConfig {

    @Bean
    public LowStockReportUseCase lowStockReportUseCase(InventoryQueryPort inventoryQueryPort) {
        return new LowStockReportService(inventoryQueryPort);
    }

    @Bean
    public WeeklySalesReportUseCase weeklySalesReportUseCase(OrderQueryPort orderQueryPort) {
        return new WeeklySalesReportService(orderQueryPort);
    }
}
