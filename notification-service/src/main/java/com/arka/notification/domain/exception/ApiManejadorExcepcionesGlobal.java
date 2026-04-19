package com.arka.notification.domain.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.arka.notification.domain.exception.dto.ApiRespuestaExcepcionDTO;


@ControllerAdvice
public class ApiManejadorExcepcionesGlobal {
	
	@ExceptionHandler(value = NotificationException.class)
	public ResponseEntity<ApiRespuestaExcepcionDTO> manejadorValidacionPeticionesException(NotificationException validacionPeticionesExcepcion){
		ApiRespuestaExcepcionDTO apiRespuesta = ApiRespuestaExcepcionDTO.builder()
				.tipoError(validacionPeticionesExcepcion.getTipoError())
				.tituloError(validacionPeticionesExcepcion.getTituloError())
				.codigoError(validacionPeticionesExcepcion.getCodigoError())
				.detalleError(validacionPeticionesExcepcion.getMessage())
				.instanciaError(validacionPeticionesExcepcion.getInstanciaError())
				.build();
		return new ResponseEntity<>(apiRespuesta, validacionPeticionesExcepcion.getHttpStatus());
	}
	
	@ExceptionHandler(value = NotFoundException.class)
	public ResponseEntity<ApiRespuestaExcepcionDTO> generarExcpcionObjetoNoEncontrado(NotFoundException excepcion){
		ApiRespuestaExcepcionDTO apiRespuesta = ApiRespuestaExcepcionDTO.builder()
				.tipoError(excepcion.getTipoError())
				.tituloError(excepcion.getTituloError())
				.codigoError(excepcion.getCodigoError())
				.detalleError(excepcion.getDetalleError())
				.instanciaError(excepcion.getInstanciaError()).build();
		return new ResponseEntity<>(apiRespuesta, NotFoundException.HTTP_STATUS);
	}
}
