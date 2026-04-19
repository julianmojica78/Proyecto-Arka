package com.arka.report.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arka.report.domain.model.OrderItemView;
import com.arka.report.domain.model.OrderView;
import com.arka.report.domain.port.out.OrderQueryPort;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class WeeklySalesReportServiceTest {

    @Mock
    private OrderQueryPort orderQueryPort;

    @Test
    void generateCsvBuildsWeeklySummary() {
        WeeklySalesReportService service = new WeeklySalesReportService(orderQueryPort);

        when(orderQueryPort.findConfirmedOrders(any(Instant.class), any(Instant.class)))
                .thenReturn(Flux.just(new OrderView(
                        1L,
                        "client-1",
                        "CONFIRMED",
                        List.of(new OrderItemView(99L, 4)),
                        Instant.now())));

        StepVerifier.create(service.generateCsv())
                .assertNext(csv -> {
                    assertThat(csv).contains("summary,total_orders,1");
                    assertThat(csv).contains("top_products,99,4");
                    assertThat(csv).contains("frequent_customers,client-1,1");
                })
                .verifyComplete();
    }
}
