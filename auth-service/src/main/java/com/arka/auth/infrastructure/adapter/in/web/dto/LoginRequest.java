package com.arka.auth.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Credenciales requeridas para autenticar un usuario.")
public class LoginRequest {

    @Schema(description = "Nombre de usuario registrado.", example = "admin")
    private String username;

    @Schema(description = "Contrasena del usuario.", example = "Admin123*", format = "password")
    private String password;
}
