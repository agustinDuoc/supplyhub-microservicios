package com.supplyhub.ms_pagos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_pagos.dto.*;
import com.supplyhub.ms_pagos.service.PagoService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/pagos")
@Slf4j
public class PagoController {

    private final PagoService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PagoResponseDTO>>> listar(@RequestHeader(value = "Authorization", required = false) String token) {
        log.info("GET /api/v1/pagos - Listando pagos");

        return ResponseEntity.ok(
                ApiResponse.<List<PagoResponseDTO>>builder()
                        .success(true)
                        .message("Pagos encontrados")
                        .data(service.listar(token))
                        .error(null)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> buscarPorId(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        log.info("GET /api/v1/pagos/{} - Buscando pago", id);

        return ResponseEntity.ok(
                ApiResponse.<PagoResponseDTO>builder()
                        .success(true)
                        .message("Pago encontrado")
                        .data(service.buscarPorId(id, token))
                        .error(null)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PagoResponseDTO>> guardar(@Valid @RequestBody PagoRequestDTO dto, @RequestHeader(value = "Authorization", required = false) String token) {
        log.info("POST /api/v1/pagos - Creando pago para orden {}", dto.getIdOrdenCompra());

        PagoResponseDTO pago = service.guardar(dto, token);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<PagoResponseDTO>builder()
                        .success(true)
                        .message("Pago creado correctamente")
                        .data(pago)
                        .error(null)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PagoRequestDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("PUT /api/v1/pagos/{} - Actualizando pago", id);

        return ResponseEntity.ok(
                ApiResponse.<PagoResponseDTO>builder()
                        .success(true)
                        .message("Pago actualizado correctamente")
                        .data(service.actualizar(id, dto, token))
                        .error(null)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        log.warn("DELETE /api/v1/pagos/{} - Eliminando pago", id);

        service.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Pago eliminado correctamente")
                        .data(null)
                        .error(null)
                        .build()
        );
    }
}
