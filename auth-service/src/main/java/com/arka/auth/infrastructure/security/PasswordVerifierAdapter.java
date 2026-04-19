package com.arka.auth.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.arka.auth.domain.port.out.PasswordVerifierPort;

@Component
public class PasswordVerifierAdapter implements PasswordVerifierPort {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public boolean matches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }

        return passwordEncoder.matches(rawPassword, storedPassword);
    }
}
