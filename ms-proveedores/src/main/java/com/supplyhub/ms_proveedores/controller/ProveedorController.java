package com.supplyhub.ms_proveedores.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_proveedores.dto.ApiResponse;
import com.supplyhub.ms_proveedores.dto.ProveedorRequestDTO;
import com.supplyhub.ms_proveedores.model.Proveedor;
import com.supplyhub.ms_proveedores.service.ProveedorService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@AllArgsConstructor
@Tag(name = "Proveedores", description = "Operaciones CRUD de proveedores")
@RestController
@RequestMapping("/api/v1/proveedores")
@Slf4j
public class ProveedorController {

    private final ProveedorService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Listar recursos", description = "Endpoint para listar recursos")
@GetMapping
    public ResponseEntity<ApiResponse<List<Proveedor>>> listar() {
        log.info("GET /api/v1/proveedores - Listando proveedores");

        return ResponseEntity.ok(
                ApiResponse.<List<Proveedor>>builder()
                        .success(true)
                        .message("Proveedores encontrados")
                        .data(service.listar())
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Buscar recurso por ID", description = "Endpoint para buscar recurso por id")
@GetMapping("/{id}")
    public ResponseEntity<EntityModel<ApiResponse<Proveedor>>> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/v1/proveedores/{} - Buscando proveedor", id);

        ApiResponse<Proveedor> response = ApiResponse.<Proveedor>builder()
                        .success(true)
                        .message("Proveedor encontrado")
                        .data(service.buscarPorId(id))
                        .error(null)
                        .build();

        EntityModel<ApiResponse<Proveedor>> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(ProveedorController.class).buscarPorId(id)).withSelfRel());
        recurso.add(linkTo(methodOn(ProveedorController.class).listar()).withRel("all"));
        recurso.add(linkTo(methodOn(ProveedorController.class).eliminar(id)).withRel("delete"));

        return ResponseEntity.ok(recurso);
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Crear recurso", description = "Endpoint para crear recurso")
@PostMapping
    public ResponseEntity<ApiResponse<Proveedor>> guardar(@Valid @RequestBody ProveedorRequestDTO dto) {
        log.info("POST /api/v1/proveedores - Creando proveedor: {}", dto.getNombre());

        Proveedor proveedor = service.guardar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Proveedor>builder()
                        .success(true)
                        .message("Proveedor creado correctamente")
                        .data(proveedor)
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Actualizar recurso", description = "Endpoint para actualizar recurso")
@PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Proveedor>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorRequestDTO dto) {

        log.info("PUT /api/v1/proveedores/{} - Actualizando proveedor", id);

        Proveedor proveedor = service.actualizar(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<Proveedor>builder()
                        .success(true)
                        .message("Proveedor actualizado correctamente")
                        .data(proveedor)
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Eliminar recurso", description = "Endpoint para eliminar recurso")
@DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        log.warn("DELETE /api/v1/proveedores/{} - Eliminando proveedor", id);

        service.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Proveedor eliminado correctamente")
                        .data(null)
                        .error(null)
                        .build()
        );
    }
}
