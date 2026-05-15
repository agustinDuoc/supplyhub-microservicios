package com.supplyhub.ms_auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username es obligatorio")
    private String username;

    @NotBlank(message = "Password es obligatorio")
    private String password;

    @JsonAlias({"rol", "role"})
    @NotBlank(message = "Role/Rol es obligatorio")
    private String role;
}