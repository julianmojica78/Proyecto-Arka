package com.arka.order.domain.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

import com.arka.order.domain.exception.dto.ApiRespuestaExcepcionDTO;

@RestControllerAdvice
public class ValidationHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidationHandler.class);

	private static final String TIPO_VALIDAR_PETICIONES = "/validaciones/peticiones";
	private static final String TIPO_VALIDAR_RECURSOS = "/validaciones/recursos";
	private static final String TIPO_VALIDAR_INTERNAL = "/error/interno";

	@ExceptionHandler(ArgumentRequiredException.class)
	public ResponseEntity<ApiRespuestaExcepcionDTO> handleArgumentRequiredException(ArgumentRequiredException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, TIPO_VALIDAR_PETICIONES, "Error validación de parámetro", "E-010",
				ex.getMessage());
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ApiRespuestaExcepcionDTO> handleNotFoundException(NotFoundException ex) {
		return buildResponse(HttpStatus.NOT_FOUND, TIPO_VALIDAR_RECURSOS, "Recurso no encontrado", "E-404",
				ex.getMessage());
	}

	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<List<ApiRespuestaExcepcionDTO>> handleWebExchangeBindException(WebExchangeBindException ex) {

		List<ApiRespuestaExcepcionDTO> errores = ex.getBindingResult().getAllErrors().stream().map(error -> {
			String detalle = error.getDefaultMessage();

			if (error instanceof FieldError fieldError) {
				detalle = String.format("%s, campo del error: %s", error.getDefaultMessage(), fieldError.getField());
			}

			return buildError(TIPO_VALIDAR_PETICIONES, "Error validación de parámetro", "E-010", detalle);
		}).toList();

		return ResponseEntity.badRequest().body(errores);
	}

	@ExceptionHandler(ServerWebInputException.class)
	public ResponseEntity<ApiRespuestaExcepcionDTO> handleServerWebInputException(ServerWebInputException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, TIPO_VALIDAR_PETICIONES, "Error de entrada en la petición",
				"E-011", ex.getReason() != null ? ex.getReason() : ex.getMessage());
	}

	@ExceptionHandler(DecodingException.class)
	public ResponseEntity<ApiRespuestaExcepcionDTO> handleDecodingException(DecodingException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, TIPO_VALIDAR_PETICIONES,
				"Error al interpretar el cuerpo de la petición", "E-012", ex.getMessage());
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiRespuestaExcepcionDTO> handleResponseStatusException(ResponseStatusException ex) {

		HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

		return buildResponse(status, resolveTipo(status.value()), "Error al procesar la petición",
				String.valueOf(status.value()), ex.getReason() != null ? ex.getReason() : ex.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiRespuestaExcepcionDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, TIPO_VALIDAR_PETICIONES, "Argumento inválido", "E-013",
				ex.getMessage());
	}

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ApiRespuestaExcepcionDTO> handleNullPointerException(NullPointerException ex) {

		LOGGER.error("Error generado por NullPointerException", ex);

		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, TIPO_VALIDAR_INTERNAL, "Error interno del servidor",
				"E-500", "Se presentó un error interno inesperado");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiRespuestaExcepcionDTO> handleException(Exception ex) {

		LOGGER.error("Error no controlado", ex);

		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, TIPO_VALIDAR_INTERNAL, "Error interno del servidor",
				"E-500", "Se presentó un error interno inesperado");
	}

	private ResponseEntity<ApiRespuestaExcepcionDTO> buildResponse(HttpStatus status, String tipo, String mensaje,
			String codigo, String detalle) {

		return ResponseEntity.status(status).body(buildError(tipo, mensaje, codigo, detalle));
	}

	private ApiRespuestaExcepcionDTO buildError(String tipo, String mensaje, String codigo, String detalle) {

		ApiRespuestaExcepcionDTO error = new ApiRespuestaExcepcionDTO(tipo, mensaje, codigo, null);
		error.setDetalleError(detalle);
		return error;
	}

	private String resolveTipo(int statusCode) {
		if (statusCode >= 400 && statusCode < 500) {
			return TIPO_VALIDAR_PETICIONES;
		}
		return TIPO_VALIDAR_INTERNAL;
	}
}