package com.arka.auth.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Repository;

import com.arka.auth.domain.model.User;
import com.arka.auth.domain.port.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository repository;

    @Override
    public Mono<User> findByUsername(String username) {

        return repository.findByUsername(username)
                .map(entity -> new User(
                        entity.getUsername(),
                        entity.getPassword(),
                        entity.getRole()
                ));
    }
}