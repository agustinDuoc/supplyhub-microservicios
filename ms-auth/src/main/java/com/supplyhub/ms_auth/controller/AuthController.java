package com.supplyhub.ms_auth.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_auth.dto.*;
import com.supplyhub.ms_auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Autenticación", description = "Operaciones de registro, login y renovación de token")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService service;

        @Operation(summary = "Registrar usuario", description = "Endpoint para registrar usuario")
@PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
        log.info("POST /auth/register - usuario: {}", req.getUsername());

        AuthResponse res = service.register(req);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Usuario registrado")
                        .data(res)
                        .error(null)
                        .build()
        );
    }

        @Operation(summary = "Iniciar sesión", description = "Endpoint para iniciar sesión")
@PostMapping("/login")
    public ResponseEntity<EntityModel<ApiResponse<AuthResponse>>> login(@Valid @RequestBody LoginRequest req) {
        log.info("POST /auth/login - usuario: {}", req.getUsername());

        AuthResponse res = service.login(req);

        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login exitoso")
                .data(res)
                .error(null)
                .build();

        EntityModel<ApiResponse<AuthResponse>> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(AuthController.class).login(null)).withSelfRel());
        recurso.add(linkTo(methodOn(AuthController.class).register(null)).withRel("register"));
        recurso.add(linkTo(methodOn(AuthController.class).refresh(null)).withRel("refresh"));

        return ResponseEntity.ok(recurso);
    }

        @Operation(summary = "Renovar token", description = "Endpoint para renovar token")
@PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest req) {

        AuthResponse res = service.refresh(req.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Token renovado")
                        .data(res)
                        .error(null)
                        .build()
        );
    }
}
