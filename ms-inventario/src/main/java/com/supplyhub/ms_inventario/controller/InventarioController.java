package com.supplyhub.ms_inventario.controller;

import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.supplyhub.ms_inventario.dto.*;
import com.supplyhub.ms_inventario.service.InventarioService;
import jakarta.validation.Valid;
import lombok.*;

@RestController
@RequestMapping("/api/v1/inventario")
@AllArgsConstructor
public class InventarioController {

    private final InventarioService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventarioResponseDTO>>> listar(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(
                ApiResponse.<List<InventarioResponseDTO>>builder()
                        .success(true)
                        .message("Inventario listado")
                        .data(service.listar(token))
                        .error(null)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> guardar(@Valid @RequestBody InventarioRequestDTO dto,
                                                                      @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<InventarioResponseDTO>builder()
                        .success(true)
                        .message("Inventario creado")
                        .data(service.guardar(dto, token))
                        .error(null)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> buscarPorId(@PathVariable Long id,
                                                                          @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(
                ApiResponse.<InventarioResponseDTO>builder()
                        .success(true)
                        .message("Inventario encontrado")
                        .data(service.buscarPorId(id, token))
                        .error(null)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> actualizar(@PathVariable Long id,
                                                                         @Valid @RequestBody InventarioRequestDTO dto,
                                                                         @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(
                ApiResponse.<InventarioResponseDTO>builder()
                        .success(true)
                        .message("Inventario actualizado")
                        .data(service.actualizar(id, dto, token))
                        .error(null)
                        .build()
        );
    }

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
