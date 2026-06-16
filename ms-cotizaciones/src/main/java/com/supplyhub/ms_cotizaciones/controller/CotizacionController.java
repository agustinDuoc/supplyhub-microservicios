package com.supplyhub.ms_cotizaciones.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.supplyhub.ms_cotizaciones.dto.*;
import com.supplyhub.ms_cotizaciones.service.CotizacionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Cotizaciones", description = "Operaciones CRUD de cotizaciones")
@RestController
@RequestMapping("/api/v1/cotizaciones")
@AllArgsConstructor
@Slf4j
public class CotizacionController {

    private final CotizacionService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Listar recursos", description = "Endpoint para listar recursos")
@GetMapping
    public ResponseEntity<ApiResponse<List<CotizacionResponseDTO>>> listar(
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(ApiResponse.<List<CotizacionResponseDTO>>builder()
                .success(true).message("Cotizaciones encontradas").data(service.listar(token)).error(null).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Buscar recurso por ID", description = "Endpoint para buscar recurso por id")
@GetMapping("/{id}")
    public ResponseEntity<EntityModel<ApiResponse<CotizacionResponseDTO>>> buscarPorId(@PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        ApiResponse<CotizacionResponseDTO> response = ApiResponse.<CotizacionResponseDTO>builder()
                .success(true).message("Cotización encontrada").data(service.buscarPorId(id, token)).error(null).build();

        EntityModel<ApiResponse<CotizacionResponseDTO>> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(CotizacionController.class).buscarPorId(id, null)).withSelfRel());
        recurso.add(linkTo(methodOn(CotizacionController.class).listar(null)).withRel("all"));
        recurso.add(linkTo(methodOn(CotizacionController.class).eliminar(id)).withRel("delete"));

        return ResponseEntity.ok(recurso);
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Crear recurso", description = "Endpoint para crear recurso")
@PostMapping
    public ResponseEntity<ApiResponse<CotizacionResponseDTO>> guardar(@Valid @RequestBody CotizacionRequestDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CotizacionResponseDTO>builder()
                .success(true).message("Cotización creada correctamente").data(service.guardar(dto, token)).error(null).build());
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Actualizar recurso", description = "Endpoint para actualizar recurso")
@PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CotizacionResponseDTO>> actualizar(@PathVariable Long id,
            @Valid @RequestBody CotizacionRequestDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(ApiResponse.<CotizacionResponseDTO>builder()
                .success(true).message("Cotización actualizada correctamente").data(service.actualizar(id, dto, token)).error(null).build());
    }

    @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Eliminar recurso", description = "Endpoint para eliminar recurso")
@DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Cotización eliminada correctamente").data(null).error(null).build());
    }
}
