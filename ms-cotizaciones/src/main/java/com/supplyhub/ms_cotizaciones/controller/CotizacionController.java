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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Cotizaciones", description = "Operaciones CRUD de cotizaciones")
@RestController
@RequestMapping("/api/v1/cotizaciones")
@AllArgsConstructor
@Slf4j
public class CotizacionController {

    private final CotizacionService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
            @Operation(summary = "Listar cotizacions", description = "Retorna la lista completa de cotizacions registrados en el sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Cotizacions encontrados","data":[{"id":1,"estado":"ACTIVO"}],"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@GetMapping
    public ResponseEntity<ApiResponse<List<CotizacionResponseDTO>>> listar(
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(ApiResponse.<List<CotizacionResponseDTO>>builder()
                .success(true).message("Cotizaciones encontradas").data(service.listar(token)).error(null).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
            @Operation(summary = "Buscar cotizacion por ID", description = "Retorna un cotizacion específico con links HATEOAS")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cotizacion encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Cotizacion encontrado","data":{"id":1,"estado":"ACTIVO"},"error":null,"_links":{"self":{"href":"/api/v1/cotizaciones/1"},"all":{"href":"/api/v1/cotizaciones"}}}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cotizacion no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Cotizacion no encontrado","data":null,"error":"Cotizacion no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
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
            @Operation(summary = "Crear cotizacion", description = "Crea un nuevo cotizacion en el sistema. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cotizacion creado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Cotizacion creado correctamente","data":{"id":1},"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Error de validación","data":null,"error":"Campo requerido no puede estar vacío"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@PostMapping
    public ResponseEntity<ApiResponse<CotizacionResponseDTO>> guardar(@Valid @RequestBody CotizacionRequestDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CotizacionResponseDTO>builder()
                .success(true).message("Cotización creada correctamente").data(service.guardar(dto, token)).error(null).build());
    }

    @PreAuthorize("hasRole('ADMIN')")
            @Operation(summary = "Actualizar cotizacion", description = "Actualiza los datos de un cotizacion existente. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cotizacion actualizado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Cotizacion actualizado correctamente","data":{"id":1},"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cotizacion no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Cotizacion no encontrado","data":null,"error":"Cotizacion no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CotizacionResponseDTO>> actualizar(@PathVariable Long id,
            @Valid @RequestBody CotizacionRequestDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(ApiResponse.<CotizacionResponseDTO>builder()
                .success(true).message("Cotización actualizada correctamente").data(service.actualizar(id, dto, token)).error(null).build());
    }

    @PreAuthorize("hasRole('ADMIN')")
            @Operation(summary = "Eliminar cotizacion", description = "Elimina un cotizacion del sistema por ID. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cotizacion eliminado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Cotizacion eliminado correctamente","data":null,"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cotizacion no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Cotizacion no encontrado","data":null,"error":"Cotizacion no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Cotización eliminada correctamente").data(null).error(null).build());
    }
}
