package com.supplyhub.ms_productos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_productos.dto.ApiResponse;
import com.supplyhub.ms_productos.dto.ProductoRequestDTO;
import com.supplyhub.ms_productos.dto.ProductoResponseDTO;
import com.supplyhub.ms_productos.service.ProductoService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@AllArgsConstructor
@Tag(name = "Productos", description = "Operaciones CRUD de productos")
@RestController
@RequestMapping("/api/v1/productos")
@Slf4j
public class ProductoController {

    private final ProductoService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Listar recursos", description = "Endpoint para listar recursos")
@GetMapping
    public ResponseEntity<ApiResponse<List<ProductoResponseDTO>>> listar(@RequestHeader(value = "Authorization", required = false) String token) {
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

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Buscar recurso por ID", description = "Endpoint para buscar recurso por id")
@GetMapping("/{id}")
    public ResponseEntity<EntityModel<ApiResponse<ProductoResponseDTO>>> buscarPorId(@PathVariable Long id,
                                                                        @RequestHeader(value = "Authorization", required = false) String token) {
        log.info("GET /api/v1/productos/{} - Buscando producto", id);

        ApiResponse<ProductoResponseDTO> response = ApiResponse.<ProductoResponseDTO>builder()
                        .success(true)
                        .message("Producto encontrado")
                        .data(service.buscarPorId(id, token))
                        .error(null)
                        .build();

        EntityModel<ApiResponse<ProductoResponseDTO>> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(ProductoController.class).buscarPorId(id, null)).withSelfRel());
        recurso.add(linkTo(methodOn(ProductoController.class).listar(null)).withRel("all"));
        recurso.add(linkTo(methodOn(ProductoController.class).eliminar(id)).withRel("delete"));

        return ResponseEntity.ok(recurso);
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Crear recurso", description = "Endpoint para crear recurso")
@PostMapping
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> guardar(@Valid @RequestBody ProductoRequestDTO dto,
                                                                    @RequestHeader(value = "Authorization", required = false) String token) {
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

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Actualizar recurso", description = "Endpoint para actualizar recurso")
@PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {

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

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Eliminar recurso", description = "Endpoint para eliminar recurso")
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
