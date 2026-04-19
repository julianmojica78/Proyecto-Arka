package com.arka.auth.domain.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ApiRespuestaExcepcionDTO {	

	private String tipoError;
	private String tituloError;
	private String codigoError;
	private String detalleError;
	private String instanciaError;
	
	public ApiRespuestaExcepcionDTO(String tipoError, String tituloError, String codigoError, String instanciaError) {
		super();
		this.tipoError = tipoError;
		this.tituloError = tituloError;
		this.codigoError = codigoError;
		this.instanciaError = instanciaError;
	}	
}
