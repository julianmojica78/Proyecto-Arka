package com.arka.inventory.domain.exception;
import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class InventoryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static final String TIPO_ERROR_VALIDACION = "/auth/";
	
	private final String tipoError;
	private final String codigoError;
	private final String tituloError;
	private final String instanciaError;
	private final String detalleError;
	private final HttpStatus httpStatus;
	
	public InventoryException() {
		super("");
		this.tituloError = "";
		this.instanciaError = "";
		this.codigoError = "";
		this.detalleError = "";
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		this.tipoError = TIPO_ERROR_VALIDACION;
	}
	
	public InventoryException(String detalleError) {
		super(detalleError);
		this.detalleError = detalleError;
		this.tituloError = "";
		this.instanciaError = "";
		this.codigoError = "";
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		this.tipoError = TIPO_ERROR_VALIDACION;
	}
	
	public InventoryException(String tituloError, String codigoError, String detalleError, String instanciaError, HttpStatus httpStatus) {
		super(detalleError);
		this.tipoError = TIPO_ERROR_VALIDACION;
		this.detalleError = detalleError;
		this.codigoError = codigoError;
		this.tituloError = tituloError;
		this.httpStatus = httpStatus;
		this.instanciaError = instanciaError;
	}
	
	public InventoryException(String mensaje, String tituloError, String codigoError, String detalleError, String instanciaError, HttpStatus httpStatus) {
		super(detalleError);
		this.tipoError = TIPO_ERROR_VALIDACION;
		this.detalleError = detalleError;
		this.codigoError = codigoError;
		this.tituloError = tituloError;
		this.httpStatus = httpStatus;
		this.instanciaError = instanciaError;
	}
}
