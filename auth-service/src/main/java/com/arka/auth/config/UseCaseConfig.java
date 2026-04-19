package com.arka.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.arka.auth.application.usecase.LoginService;
import com.arka.auth.domain.port.in.LoginUseCase;
import com.arka.auth.domain.port.out.PasswordVerifierPort;
import com.arka.auth.domain.port.out.TokenProviderPort;
import com.arka.auth.domain.port.out.UserRepositoryPort;

@Configuration
public class UseCaseConfig {

    @Bean
    public LoginUseCase loginUseCase(
            UserRepositoryPort userRepository,
            PasswordVerifierPort passwordVerifier,
            TokenProviderPort tokenProvider) {
        return new LoginService(userRepository, passwordVerifier, tokenProvider);
    }
}
