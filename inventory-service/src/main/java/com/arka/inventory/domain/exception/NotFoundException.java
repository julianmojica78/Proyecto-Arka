package com.arka.inventory.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;
	
	private final String tipoError;
	private final String codigoError;
	private final String tituloError;
	private final String instanciaError;
	private final String detalleError;
	
	public NotFoundException() {
		super("");
		this.tipoError = "";
		this.codigoError = "";
		this.tituloError = "";
		this.instanciaError = "";
		this.detalleError = "";
	}
	
	public NotFoundException(String mensaje, String tipoError, String codigoError, String tituloError, String instanciaError) {
		super(mensaje);
		this.tipoError = tipoError;
		this.codigoError = codigoError;
		this.tituloError = tituloError;
		this.instanciaError = instanciaError;
		this.detalleError = mensaje;
	}
}
