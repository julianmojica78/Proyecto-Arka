package com.arka.inventory.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private static final String[] SWAGGER_WHITELIST = {
        "/v3/api-docs",
        "/v3/api-docs/**",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/webjars/**"
};

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ReactiveJwtDecoder jwtDecoder,
            SecurityErrorResponseWriter securityErrorResponseWriter) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((exchange, ex) -> securityErrorResponseWriter.writeForbidden(
                                exchange,
                                "Debe iniciar sesion con un token valido para acceder a este recurso"))
                        .accessDeniedHandler((exchange, ex) -> securityErrorResponseWriter.writeForbidden(
                                exchange,
                                "No tiene el rol requerido para acceder a este recurso")))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(SWAGGER_WHITELIST).permitAll()
                        .pathMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/products/*/stock").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/products", "/products/**").hasAnyRole("ADMIN", "CLIENT")
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint((exchange, ex) -> securityErrorResponseWriter.writeForbidden(
                                exchange,
                                "Debe iniciar sesion con un token valido para acceder a este recurso"))
                        .accessDeniedHandler((exchange, ex) -> securityErrorResponseWriter.writeForbidden(
                                exchange,
                                "No tiene el rol requerido para acceder a este recurso"))
                        .jwt(jwt -> jwt
                                .jwtDecoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        return jwt -> Mono.just(new JwtAuthenticationToken(jwt, extractAuthorities(jwt), jwt.getSubject()));
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Object roleClaim = jwt.getClaims().get("role");
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (roleClaim instanceof Collection<?> roles) {
            roles.forEach(role -> addAuthority(authorities, role));
        } else {
            addAuthority(authorities, roleClaim);
        }

        return authorities;
    }

    private void addAuthority(List<GrantedAuthority> authorities, Object role) {
        if (role == null) {
            return;
        }

        String roleName = role.toString().trim();
        if (roleName.isBlank()) {
            return;
        }

        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        authorities.add(new SimpleGrantedAuthority(roleName));
    }

}
