package com.arka.report.infrastructure.adapter.out.http;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.arka.report.domain.model.OrderView;
import com.arka.report.domain.port.out.OrderQueryPort;

import reactor.core.publisher.Flux;

@Component
public class OrderHttpClientAdapter implements OrderQueryPort {

    private final WebClient webClient;

    public OrderHttpClientAdapter(
            WebClient.Builder webClientBuilder,
            @Value("${services.order.base-url:http://ORDER-SERVICE}") String orderBaseUrl) {
        this.webClient = webClientBuilder.baseUrl(orderBaseUrl).build();
    }

    @Override
    public Flux<OrderView> findConfirmedOrders(Instant start, Instant end) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/orders/confirmed")
                        .queryParam("start", start.toString())
                        .queryParam("end", end.toString())
                        .build())
                .retrieve()
                .bodyToFlux(OrderView.class);
    }
}
