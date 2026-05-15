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

@RestController
@RequestMapping("/api/v1/cotizaciones")
@AllArgsConstructor
@Slf4j
public class CotizacionController {

    private final CotizacionService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CotizacionResponseDTO>>> listar(
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(ApiResponse.<List<CotizacionResponseDTO>>builder()
                .success(true).message("Cotizaciones encontradas").data(service.listar(token)).error(null).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CotizacionResponseDTO>> buscarPorId(@PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(ApiResponse.<CotizacionResponseDTO>builder()
                .success(true).message("Cotización encontrada").data(service.buscarPorId(id, token)).error(null).build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CotizacionResponseDTO>> guardar(@Valid @RequestBody CotizacionRequestDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CotizacionResponseDTO>builder()
                .success(true).message("Cotización creada correctamente").data(service.guardar(dto, token)).error(null).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CotizacionResponseDTO>> actualizar(@PathVariable Long id,
            @Valid @RequestBody CotizacionRequestDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(ApiResponse.<CotizacionResponseDTO>builder()
                .success(true).message("Cotización actualizada correctamente").data(service.actualizar(id, dto, token)).error(null).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Cotización eliminada correctamente").data(null).error(null).build());
    }
}
