package com.supplyhub.ms_categorias.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_categorias.dto.ApiResponse;
import com.supplyhub.ms_categorias.dto.CategoriaRequestDTO;
import com.supplyhub.ms_categorias.model.Categoria;
import com.supplyhub.ms_categorias.service.CategoriaService;

import lombok.extern.slf4j.Slf4j;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/categorias")
@Slf4j
public class CategoriaController {

    private final CategoriaService service;

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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Categoria>> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/v1/categorias/{} - Buscando categoria", id);
        return ResponseEntity.ok(
                ApiResponse.<Categoria>builder()
                        .success(true)
                        .message("Categoría encontrada")
                        .data(service.buscarPorId(id))
                        .error(null)
                        .build()
        );
    }

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