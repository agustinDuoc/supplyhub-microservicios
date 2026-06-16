package com.supplyhub.ms_ordenes_compra.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_ordenes_compra.dto.ApiResponse;
import com.supplyhub.ms_ordenes_compra.dto.OrdenCompraRequestDTO;
import com.supplyhub.ms_ordenes_compra.dto.OrdenCompraResponseDTO;
import com.supplyhub.ms_ordenes_compra.service.OrdenCompraService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@AllArgsConstructor
@Tag(name = "Órdenes de compra", description = "Operaciones CRUD de órdenes de compra")
@RestController
@RequestMapping("/api/v1/ordenes-compra")
@Slf4j
public class OrdenCompraController {

    private final OrdenCompraService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Listar recursos", description = "Endpoint para listar recursos")
@GetMapping
    public ResponseEntity<ApiResponse<List<OrdenCompraResponseDTO>>> listar(
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("GET /api/v1/ordenes-compra - Listando órdenes de compra");

        return ResponseEntity.ok(
                ApiResponse.<List<OrdenCompraResponseDTO>>builder()
                        .success(true)
                        .message("Órdenes de compra encontradas")
                        .data(service.listar(token))
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Buscar recurso por ID", description = "Endpoint para buscar recurso por id")
@GetMapping("/{id}")
    public ResponseEntity<EntityModel<ApiResponse<OrdenCompraResponseDTO>>> buscarPorId(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("GET /api/v1/ordenes-compra/{} - Buscando orden de compra", id);

        ApiResponse<OrdenCompraResponseDTO> response = ApiResponse.<OrdenCompraResponseDTO>builder()
                        .success(true)
                        .message("Orden de compra encontrada")
                        .data(service.buscarPorId(id, token))
                        .error(null)
                        .build();

        EntityModel<ApiResponse<OrdenCompraResponseDTO>> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(OrdenCompraController.class).buscarPorId(id, null)).withSelfRel());
        recurso.add(linkTo(methodOn(OrdenCompraController.class).listar(null)).withRel("all"));
        recurso.add(linkTo(methodOn(OrdenCompraController.class).eliminar(id)).withRel("delete"));

        return ResponseEntity.ok(recurso);
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Crear recurso", description = "Endpoint para crear recurso")
@PostMapping
    public ResponseEntity<ApiResponse<OrdenCompraResponseDTO>> guardar(
            @Valid @RequestBody OrdenCompraRequestDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("POST /api/v1/ordenes-compra - Creando orden para cliente {}", dto.getIdCliente());

        OrdenCompraResponseDTO orden = service.guardar(dto, token);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<OrdenCompraResponseDTO>builder()
                        .success(true)
                        .message("Orden de compra creada correctamente")
                        .data(orden)
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Actualizar recurso", description = "Endpoint para actualizar recurso")
@PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrdenCompraResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody OrdenCompraRequestDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {

        log.info("PUT /api/v1/ordenes-compra/{} - Actualizando orden de compra", id);

        return ResponseEntity.ok(
                ApiResponse.<OrdenCompraResponseDTO>builder()
                        .success(true)
                        .message("Orden de compra actualizada correctamente")
                        .data(service.actualizar(id, dto, token))
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Eliminar recurso", description = "Endpoint para eliminar recurso")
@DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {

        log.warn("DELETE /api/v1/ordenes-compra/{} - Eliminando orden de compra", id);

        service.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Orden de compra eliminada correctamente")
                        .data(null)
                        .error(null)
                        .build()
        );
    }
}
