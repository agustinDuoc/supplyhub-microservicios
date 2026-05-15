package com.supplyhub.ms_productos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_productos.dto.ApiResponse;
import com.supplyhub.ms_productos.dto.ProductoRequestDTO;
import com.supplyhub.ms_productos.dto.ProductoResponseDTO;
import com.supplyhub.ms_productos.service.ProductoService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/productos")
@Slf4j
public class ProductoController {

    private final ProductoService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoResponseDTO>>> listar(@RequestHeader("Authorization") String token) {
        log.info("GET /api/v1/productos - Listando productos");

        return ResponseEntity.ok(
                ApiResponse.<List<ProductoResponseDTO>>builder()
                        .success(true)
                        .message("Productos encontrados")
                        .data(service.listar(token))
                        .error(null)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> buscarPorId(@PathVariable Long id,
                                                                        @RequestHeader("Authorization") String token) {
        log.info("GET /api/v1/productos/{} - Buscando producto", id);

        return ResponseEntity.ok(
                ApiResponse.<ProductoResponseDTO>builder()
                        .success(true)
                        .message("Producto encontrado")
                        .data(service.buscarPorId(id, token))
                        .error(null)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> guardar(@Valid @RequestBody ProductoRequestDTO dto,
                                                                    @RequestHeader("Authorization") String token) {
        log.info("POST /api/v1/productos - Creando producto: {}", dto.getNombre());

        ProductoResponseDTO producto = service.guardar(dto, token);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ProductoResponseDTO>builder()
                        .success(true)
                        .message("Producto creado correctamente")
                        .data(producto)
                        .error(null)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO dto,
            @RequestHeader("Authorization") String token) {

        log.info("PUT /api/v1/productos/{} - Actualizando producto", id);

        ProductoResponseDTO producto = service.actualizar(id, dto, token);

        return ResponseEntity.ok(
                ApiResponse.<ProductoResponseDTO>builder()
                        .success(true)
                        .message("Producto actualizado correctamente")
                        .data(producto)
                        .error(null)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        log.warn("DELETE /api/v1/productos/{} - Eliminando producto", id);

        service.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Producto eliminado correctamente")
                        .data(null)
                        .error(null)
                        .build()
        );
    }
}
