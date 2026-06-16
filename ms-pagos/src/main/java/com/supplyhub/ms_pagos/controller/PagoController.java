package com.supplyhub.ms_pagos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_pagos.dto.*;
import com.supplyhub.ms_pagos.service.PagoService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@AllArgsConstructor
@Tag(name = "Pagos", description = "Operaciones CRUD de pagos")
@RestController
@RequestMapping("/api/v1/pagos")
@Slf4j
public class PagoController {

    private final PagoService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Listar recursos", description = "Endpoint para listar recursos")
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

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Buscar recurso por ID", description = "Endpoint para buscar recurso por id")
@GetMapping("/{id}")
    public ResponseEntity<EntityModel<ApiResponse<PagoResponseDTO>>> buscarPorId(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        log.info("GET /api/v1/pagos/{} - Buscando pago", id);

        ApiResponse<PagoResponseDTO> response = ApiResponse.<PagoResponseDTO>builder()
                        .success(true)
                        .message("Pago encontrado")
                        .data(service.buscarPorId(id, token))
                        .error(null)
                        .build();

        EntityModel<ApiResponse<PagoResponseDTO>> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(PagoController.class).buscarPorId(id, null)).withSelfRel());
        recurso.add(linkTo(methodOn(PagoController.class).listar(null)).withRel("all"));
        recurso.add(linkTo(methodOn(PagoController.class).eliminar(id)).withRel("delete"));

        return ResponseEntity.ok(recurso);
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Crear recurso", description = "Endpoint para crear recurso")
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

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Actualizar recurso", description = "Endpoint para actualizar recurso")
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

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Eliminar recurso", description = "Endpoint para eliminar recurso")
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
