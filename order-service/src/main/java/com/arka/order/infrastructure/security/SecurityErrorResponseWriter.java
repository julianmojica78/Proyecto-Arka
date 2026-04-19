package com.arka.order.infrastructure.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.arka.order.domain.exception.OrderException;
import com.arka.order.domain.exception.dto.ApiRespuestaExcepcionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SecurityErrorResponseWriter {

    private static final String TITULO_ERROR = "Acceso denegado";
    private static final String CODIGO_ERROR = "SECURITY_403";

    private final ObjectMapper objectMapper;

    public Mono<Void> writeForbidden(ServerWebExchange exchange, String detalleError) {
        OrderException exception = new OrderException(
                TITULO_ERROR,
                CODIGO_ERROR,
                detalleError,
                exchange.getRequest().getPath().value(),
                HttpStatus.FORBIDDEN);

        ApiRespuestaExcepcionDTO body = ApiRespuestaExcepcionDTO.builder()
                .tipoError(exception.getTipoError())
                .tituloError(exception.getTituloError())
                .codigoError(exception.getCodigoError())
                .detalleError(exception.getMessage())
                .instanciaError(exception.getInstanciaError())
                .build();

        return write(exchange, body);
    }

    private Mono<Void> write(ServerWebExchange exchange, ApiRespuestaExcepcionDTO body) {
        byte[] responseBody;
        try {
            responseBody = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException ex) {
            return Mono.error(ex);
        }

        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody)));
    }
}
