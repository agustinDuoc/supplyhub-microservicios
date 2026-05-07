package com.supplyhub.ms_usuarios.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_usuarios.dto.*;
import com.supplyhub.ms_usuarios.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/usuarios")
@Slf4j
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioResponseDTO>>> listar() {
        log.info("GET /api/v1/usuarios - Listando usuarios");

        return ResponseEntity.ok(
                ApiResponse.<List<UsuarioResponseDTO>>builder()
                        .success(true)
                        .message("Usuarios encontrados")
                        .data(service.listar())
                        .error(null)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/v1/usuarios/{} - Buscando usuario", id);

        return ResponseEntity.ok(
                ApiResponse.<UsuarioResponseDTO>builder()
                        .success(true)
                        .message("Usuario encontrado")
                        .data(service.buscarPorId(id))
                        .error(null)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> guardar(@Valid @RequestBody UsuarioRequestDTO dto) {
        log.info("POST /api/v1/usuarios - Creando usuario: {}", dto.getEmail());

        UsuarioResponseDTO usuario = service.guardar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<UsuarioResponseDTO>builder()
                        .success(true)
                        .message("Usuario creado correctamente")
                        .data(usuario)
                        .error(null)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO dto) {

        log.info("PUT /api/v1/usuarios/{} - Actualizando usuario", id);

        return ResponseEntity.ok(
                ApiResponse.<UsuarioResponseDTO>builder()
                        .success(true)
                        .message("Usuario actualizado correctamente")
                        .data(service.actualizar(id, dto))
                        .error(null)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        log.warn("DELETE /api/v1/usuarios/{} - Eliminando usuario", id);

        service.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Usuario eliminado correctamente")
                        .data(null)
                        .error(null)
                        .build()
        );
    }
}
