package com.arka.auth.domain.port.out;

import com.arka.auth.domain.model.User;

import reactor.core.publisher.Mono;

public interface UserRepositoryPort {
	  Mono<User> findByUsername(String username);
}
