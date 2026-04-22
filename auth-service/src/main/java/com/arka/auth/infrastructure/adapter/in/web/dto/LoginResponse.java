package com.arka.auth.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Respuesta de autenticacion con token de acceso.")
public class LoginResponse {

    @Schema(description = "JWT firmado para consumir endpoints protegidos.", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String token;

    @Schema(description = "Tipo del token retornado.", example = "Bearer")
    private String type;
}
