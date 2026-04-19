package com.arka.report.application.usecase;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.http.HttpStatus;

import com.arka.report.domain.exception.ReportException;
import com.arka.report.domain.model.OrderItemView;
import com.arka.report.domain.model.OrderView;
import com.arka.report.domain.port.in.WeeklySalesReportUseCase;
import com.arka.report.domain.port.out.OrderQueryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class WeeklySalesReportService implements WeeklySalesReportUseCase {

	private static final String INSTANCE_REPORT = "/reports/weekly-sales";

	private final OrderQueryPort orderQueryPort;

	@Override
	public Mono<String> generateCsv() {
		Instant end = Instant.now();
		Instant start = end.minus(7, ChronoUnit.DAYS);

		return orderQueryPort.findConfirmedOrders(start, end).collectList().flatMap(orders -> {
			if (orders == null || orders.isEmpty()) {
				return Mono.error(new ReportException("Sin resultados", "REPORT_101",
						"No existen órdenes confirmadas en los últimos 7 días", INSTANCE_REPORT, HttpStatus.NOT_FOUND));
			}

			Map<Long, Integer> products = new TreeMap<>();
			Map<String, Integer> customers = new TreeMap<>();
			int totalOrders = 0;
			int totalItems = 0;

			for (OrderView order : orders) {
				if (order == null) {
					throw new ReportException("Error de datos", "REPORT_102",
							"Se encontró una orden nula en la fuente de datos", INSTANCE_REPORT,
							HttpStatus.INTERNAL_SERVER_ERROR);
				}

				totalOrders++;
				customers.merge(normalize(order.getCustomerId()), 1, Integer::sum);

				if (order.getItems() != null) {
					for (OrderItemView item : order.getItems()) {
						if (item == null) {
							throw new ReportException("Error de datos", "REPORT_103",
									"Se encontró un item nulo en una orden", INSTANCE_REPORT,
									HttpStatus.INTERNAL_SERVER_ERROR);
						}

						if (item.getProductId() == null) {
							throw new ReportException("Error de datos", "REPORT_104",
									"Se encontró un item sin productId", INSTANCE_REPORT,
									HttpStatus.INTERNAL_SERVER_ERROR);
						}

						if (item.getQuantity() == null) {
							throw new ReportException("Error de datos", "REPORT_105",
									"Se encontró un item sin quantity", INSTANCE_REPORT,
									HttpStatus.INTERNAL_SERVER_ERROR);
						}

						if (item.getQuantity() < 0) {
							throw new ReportException("Error de datos", "REPORT_106",
									"Se encontró un item con quantity negativa", INSTANCE_REPORT,
									HttpStatus.INTERNAL_SERVER_ERROR);
						}

						products.merge(item.getProductId(), item.getQuantity(), Integer::sum);
						totalItems += item.getQuantity();
					}
				}
			}

			StringBuilder csv = new StringBuilder("section,key,value\n");
			csv.append("summary,start,").append(start).append('\n');
			csv.append("summary,end,").append(end).append('\n');
			csv.append("summary,total_orders,").append(totalOrders).append('\n');
			csv.append("summary,total_items,").append(totalItems).append('\n');

			products.forEach((productId, quantity) -> csv.append("top_products,").append(productId).append(',')
					.append(quantity).append('\n'));

			customers.forEach((customerId, count) -> csv.append("frequent_customers,").append(customerId).append(',')
					.append(count).append('\n'));

			return Mono.just(csv.toString());
		}).onErrorMap(ex -> {
			if (ex instanceof ReportException) {
				return ex;
			}

			return new ReportException("Error generando reporte semanal", "REPORT_107",
					"Ocurrió un error generando el reporte semanal de ventas: " + ex.getMessage(), INSTANCE_REPORT,
					HttpStatus.INTERNAL_SERVER_ERROR);
		});
	}

	private String normalize(String customerId) {
		return customerId == null || customerId.isBlank() ? "anonymous" : customerId;
	}
}