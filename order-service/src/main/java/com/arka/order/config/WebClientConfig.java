package com.arka.order.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public WebClient.Builder webClientBuilder(ExchangeFilterFunction bearerTokenRelayFilter) {
        return WebClient.builder().filter(bearerTokenRelayFilter);
    }

    @Bean
    @LoadBalanced
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public WebClient.Builder loadBalancedWebClientBuilder(ExchangeFilterFunction bearerTokenRelayFilter) {
        return WebClient.builder().filter(bearerTokenRelayFilter);
    }

    @Bean
    public ExchangeFilterFunction bearerTokenRelayFilter() {
        return (request, next) -> withBearerToken(request).flatMap(next::exchange);
    }

    private Mono<ClientRequest> withBearerToken(ClientRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(JwtAuthenticationToken.class::isInstance)
                .cast(JwtAuthenticationToken.class)
                .map(authentication -> ClientRequest.from(request)
                        .headers(headers -> headers.setBearerAuth(authentication.getToken().getTokenValue()))
                        .build())
                .defaultIfEmpty(request);
    }
}
