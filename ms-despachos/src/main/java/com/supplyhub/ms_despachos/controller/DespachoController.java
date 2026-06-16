package com.supplyhub.ms_despachos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_despachos.dto.*;
import com.supplyhub.ms_despachos.service.DespachoService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@AllArgsConstructor
@Tag(name = "Despachos", description = "Operaciones CRUD de despachos")
@RestController
@RequestMapping("/api/v1/despachos")
@Slf4j
public class DespachoController {

    private final DespachoService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Listar recursos", description = "Endpoint para listar recursos")
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

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Buscar recurso por ID", description = "Endpoint para buscar recurso por id")
@GetMapping("/{id}")
    public ResponseEntity<EntityModel<ApiResponse<DespachoResponseDTO>>> buscarPorId(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        log.info("GET /api/v1/despachos/{} - Buscando despacho", id);

        ApiResponse<DespachoResponseDTO> response = ApiResponse.<DespachoResponseDTO>builder()
                        .success(true)
                        .message("Despacho encontrado")
                        .data(service.buscarPorId(id, token))
                        .error(null)
                        .build();

        EntityModel<ApiResponse<DespachoResponseDTO>> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(DespachoController.class).buscarPorId(id, null)).withSelfRel());
        recurso.add(linkTo(methodOn(DespachoController.class).listar(null)).withRel("all"));
        recurso.add(linkTo(methodOn(DespachoController.class).eliminar(id)).withRel("delete"));

        return ResponseEntity.ok(recurso);
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Crear recurso", description = "Endpoint para crear recurso")
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

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Actualizar recurso", description = "Endpoint para actualizar recurso")
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

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Eliminar recurso", description = "Endpoint para eliminar recurso")
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
