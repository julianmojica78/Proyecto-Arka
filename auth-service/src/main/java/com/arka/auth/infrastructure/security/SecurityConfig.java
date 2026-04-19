package com.arka.auth.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            SecurityErrorResponseWriter securityErrorResponseWriter) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((exchange, ex) -> securityErrorResponseWriter.writeForbidden(
                                exchange,
                                "Debe iniciar sesion con un token valido para acceder a este recurso"))
                        .accessDeniedHandler((exchange, ex) -> securityErrorResponseWriter.writeForbidden(
                                exchange,
                                "No tiene el rol requerido para acceder a este recurso")))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/auth/login", "/auth/health").permitAll()
                        .anyExchange().denyAll()
                )
                .build();
    }
}
