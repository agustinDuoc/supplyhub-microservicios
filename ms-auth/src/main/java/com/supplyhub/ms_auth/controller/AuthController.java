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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Autenticación", description = "Operaciones de registro, login y renovación de token")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService service;

    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario con rol CLIENTE o ADMIN y retorna los tokens JWT de acceso y refresco")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Usuario registrado","data":{"accessToken":"eyJhbGciOiJIUzI1NiJ9...","refreshToken":"eyJhbGciOiJIUzI1NiJ9...","username":"jdoe","role":"ROLE_CLIENTE"},"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Error de validación","data":null,"error":"El username no puede estar vacío"}
                """)))
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
        validarTexto(req.getUsername(), "username");
        validarTexto(req.getPassword(), "password");
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

    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario con username y password, retorna tokens JWT y links HATEOAS")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Login exitoso","data":{"accessToken":"eyJhbGciOiJIUzI1NiJ9...","refreshToken":"eyJhbGciOiJIUzI1NiJ9...","username":"admin","role":"ROLE_ADMIN"},"error":null,"_links":{"self":{"href":"/auth/login"},"register":{"href":"/auth/register"},"refresh":{"href":"/auth/refresh"}}}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales incorrectas",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Credenciales inválidas","data":null,"error":"Bad credentials"}
                """)))
    })
    @PostMapping("/login")
    public ResponseEntity<EntityModel<ApiResponse<AuthResponse>>> login(@Valid @RequestBody LoginRequest req) {
        validarTexto(req.getUsername(), "username");
        validarTexto(req.getPassword(), "password");
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

    @Operation(summary = "Renovar token", description = "Genera un nuevo accessToken usando un refreshToken válido")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token renovado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Token renovado","data":{"accessToken":"eyJhbGciOiJIUzI1NiJ9...nuevo...","refreshToken":"eyJhbGciOiJIUzI1NiJ9...","username":"admin","role":"ROLE_ADMIN"},"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Token inválido","data":null,"error":"JWT expired"}
                """)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest req) {
        validarTexto(req.getRefreshToken(), "refreshToken");

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

    private void validarTexto(String valor, String campo) {
        if (valor == null) {
            String mensaje = "El campo " + campo + " es obligatorio";
            throw new IllegalArgumentException(mensaje);
        }
        if (valor.isBlank()) {
            String mensaje = "El campo " + campo + " no puede estar vacío";
            String detalle = "Valor recibido vacío";
            throw new IllegalArgumentException(mensaje + ". " + detalle);
        }
    }
}
