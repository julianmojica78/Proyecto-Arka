package com.arka.gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SecurityErrorResponseWriter {

    private static final String TIPO_ERROR = "/auth/";
    private static final String TITULO_ERROR = "Acceso denegado";
    private static final String CODIGO_ERROR = "SECURITY_403";

    private final ObjectMapper objectMapper;

    public Mono<Void> writeForbidden(ServerWebExchange exchange, String detalleError) {
        ApiRespuestaExcepcionDTO body = new ApiRespuestaExcepcionDTO(
                TIPO_ERROR,
                TITULO_ERROR,
                CODIGO_ERROR,
                detalleError,
                exchange.getRequest().getPath().value());

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

    private record ApiRespuestaExcepcionDTO(
            String tipoError,
            String tituloError,
            String codigoError,
            String detalleError,
            String instanciaError) {
    }
}
