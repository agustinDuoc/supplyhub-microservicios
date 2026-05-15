package com.supplyhub.ms_despachos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_despachos.dto.*;
import com.supplyhub.ms_despachos.service.DespachoService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/despachos")
@Slf4j
public class DespachoController {

    private final DespachoService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DespachoResponseDTO>>> listar(@RequestHeader(value = "Authorization", required = false) String token) {
        log.info("GET /api/v1/despachos - Listando despachos");

        return ResponseEntity.ok(
                ApiResponse.<List<DespachoResponseDTO>>builder()
                        .success(true)
                        .message("Despachos encontrados")
                        .data(service.listar(token))
                        .error(null)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DespachoResponseDTO>> buscarPorId(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        log.info("GET /api/v1/despachos/{} - Buscando despacho", id);

        return ResponseEntity.ok(
                ApiResponse.<DespachoResponseDTO>builder()
                        .success(true)
                        .message("Despacho encontrado")
                        .data(service.buscarPorId(id, token))
                        .error(null)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DespachoResponseDTO>> guardar(@Valid @RequestBody DespachoRequestDTO dto, @RequestHeader(value = "Authorization", required = false) String token) {
        log.info("POST /api/v1/despachos - Creando despacho para orden {}", dto.getIdOrdenCompra());

        DespachoResponseDTO despacho = service.guardar(dto, token);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<DespachoResponseDTO>builder()
                        .success(true)
                        .message("Despacho creado correctamente")
                        .data(despacho)
                        .error(null)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DespachoResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DespachoRequestDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("PUT /api/v1/despachos/{} - Actualizando despacho", id);

        return ResponseEntity.ok(
                ApiResponse.<DespachoResponseDTO>builder()
                        .success(true)
                        .message("Despacho actualizado correctamente")
                        .data(service.actualizar(id, dto, token))
                        .error(null)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        log.warn("DELETE /api/v1/despachos/{} - Eliminando despacho", id);

        service.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Despacho eliminado correctamente")
                        .data(null)
                        .error(null)
                        .build()
        );
    }
}
