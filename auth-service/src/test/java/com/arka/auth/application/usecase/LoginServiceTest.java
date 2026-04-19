package com.arka.auth.application.usecase;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arka.auth.domain.exception.AuthException;
import com.arka.auth.domain.model.User;
import com.arka.auth.domain.port.out.PasswordVerifierPort;
import com.arka.auth.domain.port.out.TokenProviderPort;
import com.arka.auth.domain.port.out.UserRepositoryPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordVerifierPort passwordVerifier;

    @Mock
    private TokenProviderPort tokenProvider;

    @Test
    void loginReturnsTokenWhenCredentialsAreValid() {
        LoginService service = new LoginService(userRepository, passwordVerifier, tokenProvider);
        User user = new User("admin", "hash", "ROLE_ADMIN");

        when(userRepository.findByUsername("admin")).thenReturn(Mono.just(user));
        when(passwordVerifier.matches("1234", "hash")).thenReturn(true);
        when(tokenProvider.generateToken("admin", "ROLE_ADMIN")).thenReturn("jwt-token");

        StepVerifier.create(service.login("admin", "1234"))
                .expectNext("jwt-token")
                .verifyComplete();

        verify(tokenProvider).generateToken("admin", "ROLE_ADMIN");
    }

    @Test
    void loginFailsWhenPasswordDoesNotMatch() {
        LoginService service = new LoginService(userRepository, passwordVerifier, tokenProvider);
        User user = new User("admin", "hash", "ROLE_ADMIN");

        when(userRepository.findByUsername("admin")).thenReturn(Mono.just(user));
        when(passwordVerifier.matches("bad", "hash")).thenReturn(false);

        StepVerifier.create(service.login("admin", "bad"))
                .expectError(AuthException.class)
                .verify();

        verifyNoInteractions(tokenProvider);
    }
}
