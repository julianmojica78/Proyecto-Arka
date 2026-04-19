package com.arka.auth.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.arka.auth.domain.exception.AuthException;
import com.arka.auth.domain.exception.dto.ApiRespuestaExcepcionDTO;
import com.arka.auth.domain.port.in.LoginUseCase;
import com.arka.auth.infrastructure.adapter.in.web.dto.LoginRequest;
import com.arka.auth.infrastructure.adapter.in.web.dto.LoginResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthHandler {

	private final LoginUseCase loginUseCase;

	public Mono<ServerResponse> login(ServerRequest request) {
		return request.bodyToMono(LoginRequest.class)
				.flatMap(req -> loginUseCase.login(req.getUsername(), req.getPassword()))
				.flatMap(token -> ServerResponse.ok().bodyValue(new LoginResponse(token, "Bearer")))
				.onErrorResume(AuthException.class, ex -> {
					ApiRespuestaExcepcionDTO error = ApiRespuestaExcepcionDTO.builder().tipoError(ex.getTipoError())
							.tituloError(ex.getTituloError()).codigoError(ex.getCodigoError())
							.detalleError(ex.getMessage()).instanciaError(ex.getInstanciaError()).build();

					return ServerResponse.status(ex.getHttpStatus()).bodyValue(error);
				}).onErrorResume(Exception.class, ex -> {
					ApiRespuestaExcepcionDTO error = ApiRespuestaExcepcionDTO.builder().tipoError("/error/interno")
							.tituloError("Error interno del servidor").codigoError("E-500")
							.detalleError("Se presentó un error interno inesperado").instanciaError("AuthHandler.login")
							.build();

					return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(error);
				});
	}

	public Mono<ServerResponse> health(ServerRequest request) {
		return ServerResponse.ok().bodyValue("Auth service OK");
	}
}