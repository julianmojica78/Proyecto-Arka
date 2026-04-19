package com.arka.auth.application.usecase;

import org.springframework.http.HttpStatus;

import com.arka.auth.domain.exception.AuthException;
import com.arka.auth.domain.port.in.LoginUseCase;
import com.arka.auth.domain.port.out.PasswordVerifierPort;
import com.arka.auth.domain.port.out.TokenProviderPort;
import com.arka.auth.domain.port.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordVerifierPort passwordVerifier;
    private final TokenProviderPort tokenProvider;

    @Override
    public Mono<String> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordVerifier.matches(password, user.getPassword()))
                .map(user -> tokenProvider.generateToken(user.getUsername(), user.getRole()))
                .switchIfEmpty(Mono.error(new AuthException("Error Credenciales", "E-10", "Credenciales Incorrectas", "LoginService.login", HttpStatus.UNAUTHORIZED)));
    }
}
