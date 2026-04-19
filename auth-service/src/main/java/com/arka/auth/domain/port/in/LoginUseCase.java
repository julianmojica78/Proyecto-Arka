package com.arka.auth.domain.port.in;

import reactor.core.publisher.Mono;

public interface LoginUseCase {
	 Mono<String> login(String username, String password);
}
