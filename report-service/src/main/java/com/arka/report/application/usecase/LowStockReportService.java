package com.arka.report.application.usecase;

import org.springframework.http.HttpStatus;

import com.arka.report.domain.exception.ReportException;
import com.arka.report.domain.port.in.LowStockReportUseCase;
import com.arka.report.domain.port.out.InventoryQueryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LowStockReportService implements LowStockReportUseCase {

	private static final int DEFAULT_THRESHOLD = 10;
	private static final String INSTANCE_REPORT = "/reports/low-stock";

	private final InventoryQueryPort inventoryQueryPort;

	@Override
	public Mono<String> generateCsv(Integer threshold) {

		int effectiveThreshold = threshold != null ? threshold : DEFAULT_THRESHOLD;

		// 🔴 Validación
		if (effectiveThreshold < 0) {
			return Mono.error(new ReportException("Error de validación", "REPORT_001",
					"El threshold no puede ser negativo", INSTANCE_REPORT, HttpStatus.BAD_REQUEST));
		}

		return inventoryQueryPort.findAllProducts()

				// 🔥 Validación defensiva de datos
				.filter(product -> product.getStock() != null && product.getStock() < effectiveThreshold)

				.map(product -> {
					if (product.getId() == null) {
						throw new ReportException("Error de datos", "REPORT_002", "Producto sin id", INSTANCE_REPORT,
								HttpStatus.INTERNAL_SERVER_ERROR);
					}

					return String.join(",", product.getId().toString(), quote(product.getName()),
							quote(product.getCategory()), product.getStock().toString(), product.getPrice().toString());
				})

				.collectList()

				// 🔥 Caso sin datos (opcional pero recomendado)
				.flatMap(lines -> {
					if (lines.isEmpty()) {
						return Mono.error(new ReportException("Sin resultados", "REPORT_003",
								"No existen productos con bajo stock", INSTANCE_REPORT, HttpStatus.NOT_FOUND));
					}

					StringBuilder csv = new StringBuilder("id,name,category,stock,price\n");
					lines.forEach(line -> csv.append(line).append('\n'));
					return Mono.just(csv.toString());
				})

				// 🔥 Manejo global de errores
				.onErrorMap(ex -> {
					if (ex instanceof ReportException) {
						return ex;
					}

					return new ReportException("Error generando reporte", "REPORT_004",
							"Ocurrió un error generando el CSV: " + ex.getMessage(), INSTANCE_REPORT,
							HttpStatus.INTERNAL_SERVER_ERROR);
				});
	}

	private String quote(String value) {
		return "\"" + (value == null ? "" : value.replace("\"", "\"\"")) + "\"";
	}
}