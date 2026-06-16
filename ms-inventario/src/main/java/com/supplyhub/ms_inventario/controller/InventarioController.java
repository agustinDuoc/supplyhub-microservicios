package com.supplyhub.ms_inventario.controller;

import java.util.List;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.supplyhub.ms_inventario.dto.*;
import com.supplyhub.ms_inventario.service.InventarioService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Inventario", description = "Operaciones CRUD de inventario")
@RestController
@RequestMapping("/api/v1/inventario")
@AllArgsConstructor
public class InventarioController {

    private final InventarioService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Listar recursos", description = "Endpoint para listar recursos")
@GetMapping
    public ResponseEntity<ApiResponse<List<InventarioResponseDTO>>> listar(@RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(
                ApiResponse.<List<InventarioResponseDTO>>builder()
                        .success(true)
                        .message("Inventario listado")
                        .data(service.listar(token))
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Crear recurso", description = "Endpoint para crear recurso")
@PostMapping
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> guardar(@Valid @RequestBody InventarioRequestDTO dto,
                                                                      @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<InventarioResponseDTO>builder()
                        .success(true)
                        .message("Inventario creado")
                        .data(service.guardar(dto, token))
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Buscar recurso por ID", description = "Endpoint para buscar recurso por id")
@GetMapping("/{id}")
    public ResponseEntity<EntityModel<ApiResponse<InventarioResponseDTO>>> buscarPorId(@PathVariable Long id,
                                                                          @RequestHeader(value = "Authorization", required = false) String token) {
        ApiResponse<InventarioResponseDTO> response = ApiResponse.<InventarioResponseDTO>builder()
                        .success(true)
                        .message("Inventario encontrado")
                        .data(service.buscarPorId(id, token))
                        .error(null)
                        .build();

        EntityModel<ApiResponse<InventarioResponseDTO>> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(InventarioController.class).buscarPorId(id, null)).withSelfRel());
        recurso.add(linkTo(methodOn(InventarioController.class).listar(null)).withRel("all"));
        recurso.add(linkTo(methodOn(InventarioController.class).eliminar(id)).withRel("delete"));

        return ResponseEntity.ok(recurso);
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Actualizar recurso", description = "Endpoint para actualizar recurso")
@PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> actualizar(@PathVariable Long id,
                                                                         @Valid @RequestBody InventarioRequestDTO dto,
                                                                         @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(
                ApiResponse.<InventarioResponseDTO>builder()
                        .success(true)
                        .message("Inventario actualizado")
                        .data(service.actualizar(id, dto, token))
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Eliminar recurso", description = "Endpoint para eliminar recurso")
@DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Inventario eliminado")
                        .data(null)
                        .error(null)
                        .build()
        );
    }
}
