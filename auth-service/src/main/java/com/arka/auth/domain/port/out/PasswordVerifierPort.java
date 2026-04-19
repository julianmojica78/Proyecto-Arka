package com.arka.auth.domain.port.out;

public interface PasswordVerifierPort {

    boolean matches(String rawPassword, String storedPassword);
}
