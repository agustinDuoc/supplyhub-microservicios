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
    public ResponseEntity<ApiResponse<List<InventarioResponseDTO>>> listar() {
        return ResponseEntity.ok(
                ApiResponse.<List<InventarioResponseDTO>>builder()
                        .success(true)
                        .message("Inventario obtenido")
                        .data(service.listar())
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> guardar(@Valid @RequestBody InventarioRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<InventarioResponseDTO>builder()
                        .success(true)
                        .message("Inventario creado")
                        .data(service.guardar(dto))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> buscarPorId(@PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<InventarioResponseDTO>builder()
                        .success(true)
                        .message("Inventario encontrado")
                        .data(service.buscarPorId(id))
                        .error(null)
                        .build()
        );
}
}
