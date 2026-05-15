package com.supplyhub.ms_cotizaciones.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_cotizaciones.dto.*;
import com.supplyhub.ms_cotizaciones.service.CotizacionService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/cotizaciones")
@Slf4j
public class CotizacionController {

    private final CotizacionService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CotizacionResponseDTO>>> listar() {
        log.info("GET /api/v1/cotizaciones - Listando cotizaciones");

        return ResponseEntity.ok(
                ApiResponse.<List<CotizacionResponseDTO>>builder()
                        .success(true)
                        .message("Cotizaciones encontradas")
                        .data(service.listar())
                        .error(null)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CotizacionResponseDTO>> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/v1/cotizaciones/{} - Buscando cotización", id);

        return ResponseEntity.ok(
                ApiResponse.<CotizacionResponseDTO>builder()
                        .success(true)
                        .message("Cotización encontrada")
                        .data(service.buscarPorId(id))
                        .error(null)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CotizacionResponseDTO>> guardar(@Valid @RequestBody CotizacionRequestDTO dto) {
        log.info("POST /api/v1/cotizaciones - Creando cotización para producto {}", dto.getIdProducto());

        CotizacionResponseDTO cotizacion = service.guardar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CotizacionResponseDTO>builder()
                        .success(true)
                        .message("Cotización creada correctamente")
                        .data(cotizacion)
                        .error(null)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CotizacionResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CotizacionRequestDTO dto) {

        log.info("PUT /api/v1/cotizaciones/{} - Actualizando cotización", id);

        return ResponseEntity.ok(
                ApiResponse.<CotizacionResponseDTO>builder()
                        .success(true)
                        .message("Cotización actualizada correctamente")
                        .data(service.actualizar(id, dto))
                        .error(null)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        log.warn("DELETE /api/v1/cotizaciones/{} - Eliminando cotización", id);

        service.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Cotización eliminada correctamente")
                        .data(null)
                        .error(null)
                        .build()
        );
    }
}
