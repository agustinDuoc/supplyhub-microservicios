package com.supplyhub.ms_ordenes_compra.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_ordenes_compra.dto.ApiResponse;
import com.supplyhub.ms_ordenes_compra.dto.OrdenCompraRequestDTO;
import com.supplyhub.ms_ordenes_compra.dto.OrdenCompraResponseDTO;
import com.supplyhub.ms_ordenes_compra.service.OrdenCompraService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/ordenes-compra")
@Slf4j
public class OrdenCompraController {

    private final OrdenCompraService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrdenCompraResponseDTO>>> listar(
            @RequestHeader("Authorization") String token) {

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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrdenCompraResponseDTO>> buscarPorId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        log.info("GET /api/v1/ordenes-compra/{} - Buscando orden de compra", id);

        return ResponseEntity.ok(
                ApiResponse.<OrdenCompraResponseDTO>builder()
                        .success(true)
                        .message("Orden de compra encontrada")
                        .data(service.buscarPorId(id, token))
                        .error(null)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrdenCompraResponseDTO>> guardar(
            @Valid @RequestBody OrdenCompraRequestDTO dto,
            @RequestHeader("Authorization") String token) {

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

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrdenCompraResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody OrdenCompraRequestDTO dto,
            @RequestHeader("Authorization") String token) {

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
