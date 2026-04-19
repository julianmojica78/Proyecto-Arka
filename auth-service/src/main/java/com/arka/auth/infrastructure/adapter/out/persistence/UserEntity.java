package com.arka.auth.infrastructure.adapter.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("users")
public class UserEntity {

    @Id
    private Long id;
    private String username;
    private String password;
    private String role;
}