package com.supplyhub.ms_categorias.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_categorias.dto.ApiResponse;
import com.supplyhub.ms_categorias.dto.CategoriaRequestDTO;
import com.supplyhub.ms_categorias.model.Categoria;
import com.supplyhub.ms_categorias.service.CategoriaService;

import lombok.extern.slf4j.Slf4j;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@AllArgsConstructor
@Tag(name = "Categorías", description = "Operaciones CRUD de categorías")
@RestController
@RequestMapping("/api/v1/categorias")
@Slf4j
public class CategoriaController {

    private final CategoriaService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Listar recursos", description = "Endpoint para listar recursos")
@GetMapping
    public ResponseEntity<ApiResponse<List<Categoria>>> listar() {
        log.info("GET /api/v1/categorias - Listado categorias");
        return ResponseEntity.ok(
                ApiResponse.<List<Categoria>>builder()
                        .success(true)
                        .message("Categorías encontradas")
                        .data(service.listar())
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Buscar recurso por ID", description = "Endpoint para buscar recurso por id")
@GetMapping("/{id}")
    public ResponseEntity<EntityModel<ApiResponse<Categoria>>> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/v1/categorias/{} - Buscando categoria", id);
        ApiResponse<Categoria> response = ApiResponse.<Categoria>builder()
                        .success(true)
                        .message("Categoría encontrada")
                        .data(service.buscarPorId(id))
                        .error(null)
                        .build();

        EntityModel<ApiResponse<Categoria>> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(CategoriaController.class).buscarPorId(id)).withSelfRel());
        recurso.add(linkTo(methodOn(CategoriaController.class).listar()).withRel("all"));
        recurso.add(linkTo(methodOn(CategoriaController.class).eliminar(id)).withRel("delete"));

        return ResponseEntity.ok(recurso);
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Crear recurso", description = "Endpoint para crear recurso")
@PostMapping
    public ResponseEntity<ApiResponse<Categoria>> guardar(@Valid @RequestBody CategoriaRequestDTO dto) {
        log.info("POST /api/v1/categorias - Creando categoría: {}", dto.getNombre());
        Categoria categoria = service.guardar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Categoria>builder()
                        .success(true)
                        .message("Categoría creada correctamente")
                        .data(categoria)
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Actualizar recurso", description = "Endpoint para actualizar recurso")
@PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Categoria>> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO dto) {
        log.info("PUT /api/v1/categorias/{} - Actualizando categoría", id);
        Categoria categoria = service.actualizar(id, dto);
        return ResponseEntity.ok(
                ApiResponse.<Categoria>builder()
                        .success(true)
                        .message("Categoría actualizada correctamente")
                        .data(categoria)
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Eliminar recurso", description = "Endpoint para eliminar recurso")
@DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        log.warn("DELETE /api/v1/categorias/{} - Eliminando categoría", id);
        service.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Categoría eliminada correctamente")
                        .data(null)
                        .error(null)
                        .build()
        );
    }
}
