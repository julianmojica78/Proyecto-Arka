package com.arka.report.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arka.report.domain.model.ProductView;
import com.arka.report.domain.port.out.InventoryQueryPort;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class LowStockReportServiceTest {

    @Mock
    private InventoryQueryPort inventoryQueryPort;

    @Test
    void generateCsvFiltersLowStockProducts() {
        LowStockReportService service = new LowStockReportService(inventoryQueryPort);

        when(inventoryQueryPort.findAllProducts()).thenReturn(Flux.just(
                new ProductView(1L, "Mouse", "Wireless", BigDecimal.TEN, 2, "TECH"),
                new ProductView(2L, "Laptop", "Gaming", BigDecimal.valueOf(5000), 20, "TECH")));

        StepVerifier.create(service.generateCsv(5))
                .assertNext(csv -> {
                    assertThat(csv).contains("\"Mouse\"");
                    assertThat(csv).doesNotContain("\"Laptop\"");
                })
                .verifyComplete();
    }
}
