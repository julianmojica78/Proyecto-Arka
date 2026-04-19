package com.arka.auth.domain.port.out;

public interface TokenProviderPort {

    String generateToken(String username, String role);
}
