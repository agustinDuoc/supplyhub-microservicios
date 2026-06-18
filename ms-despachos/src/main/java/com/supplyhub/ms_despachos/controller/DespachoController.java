package com.supplyhub.ms_despachos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_despachos.dto.*;
import com.supplyhub.ms_despachos.service.DespachoService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@AllArgsConstructor
@Tag(name = "Despachos", description = "Operaciones CRUD de despachos")
@RestController
@RequestMapping("/api/v1/despachos")
@Slf4j
public class DespachoController {

    private final DespachoService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
            @Operation(summary = "Listar despachos", description = "Retorna la lista completa de despachos registrados en el sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Despachos encontrados","data":[{"id":1,"estado":"ACTIVO"}],"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@GetMapping
    public ResponseEntity<ApiResponse<List<DespachoResponseDTO>>> listar(@RequestHeader(value = "Authorization", required = false) String token) {
        log.info("GET /api/v1/despachos - Listando despachos");

        return ResponseEntity.ok(
                ApiResponse.<List<DespachoResponseDTO>>builder()
                        .success(true)
                        .message("Despachos encontrados")
                        .data(service.listar(token))
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
            @Operation(summary = "Buscar despacho por ID", description = "Retorna un despacho específico con links HATEOAS")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Despacho encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Despacho encontrado","data":{"id":1,"estado":"ACTIVO"},"error":null,"_links":{"self":{"href":"/api/v1/despachos/1"},"all":{"href":"/api/v1/despachos"}}}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Despacho no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Despacho no encontrado","data":null,"error":"Despacho no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@GetMapping("/{id}")
    public ResponseEntity<EntityModel<ApiResponse<DespachoResponseDTO>>> buscarPorId(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        validarId(id);
        log.info("GET /api/v1/despachos/{} - Buscando despacho", id);

        ApiResponse<DespachoResponseDTO> response = ApiResponse.<DespachoResponseDTO>builder()
                        .success(true)
                        .message("Despacho encontrado")
                        .data(service.buscarPorId(id, token))
                        .error(null)
                        .build();

        EntityModel<ApiResponse<DespachoResponseDTO>> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(DespachoController.class).buscarPorId(id, null)).withSelfRel());
        recurso.add(linkTo(methodOn(DespachoController.class).listar(null)).withRel("all"));
        recurso.add(linkTo(methodOn(DespachoController.class).actualizar(id, null, null)).withRel("update"));
        recurso.add(linkTo(methodOn(DespachoController.class).eliminar(id)).withRel("delete"));

        return ResponseEntity.ok(recurso);
    }

    @PreAuthorize("hasRole('ADMIN')")
            @Operation(summary = "Crear despacho", description = "Crea un nuevo despacho en el sistema. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Despacho creado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Despacho creado correctamente","data":{"id":1},"error":null}
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
    public ResponseEntity<ApiResponse<DespachoResponseDTO>> guardar(@Valid @RequestBody DespachoRequestDTO dto, @RequestHeader(value = "Authorization", required = false) String token) {
        log.info("POST /api/v1/despachos - Creando despacho para orden {}", dto.getIdOrdenCompra());

        DespachoResponseDTO despacho = service.guardar(dto, token);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<DespachoResponseDTO>builder()
                        .success(true)
                        .message("Despacho creado correctamente")
                        .data(despacho)
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
            @Operation(summary = "Actualizar despacho", description = "Actualiza los datos de un despacho existente. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Despacho actualizado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Despacho actualizado correctamente","data":{"id":1},"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Despacho no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Despacho no encontrado","data":null,"error":"Despacho no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DespachoResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DespachoRequestDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {

        validarId(id);
        log.info("PUT /api/v1/despachos/{} - Actualizando despacho", id);

        return ResponseEntity.ok(
                ApiResponse.<DespachoResponseDTO>builder()
                        .success(true)
                        .message("Despacho actualizado correctamente")
                        .data(service.actualizar(id, dto, token))
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
            @Operation(summary = "Eliminar despacho", description = "Elimina un despacho del sistema por ID. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Despacho eliminado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Despacho eliminado correctamente","data":null,"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Despacho no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Despacho no encontrado","data":null,"error":"Despacho no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        validarId(id);
        log.warn("DELETE /api/v1/despachos/{} - Eliminando despacho", id);

        service.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Despacho eliminado correctamente")
                        .data(null)
                        .error(null)
                        .build()
        );
    }

    private void validarId(Long id) {
        if (id == null) {
            String mensaje = "El id del despacho es obligatorio";
            throw new IllegalArgumentException(mensaje);
        }
        if (id <= 0) {
            String mensaje = "El id del despacho debe ser positivo";
            throw new IllegalArgumentException(mensaje);
        }
        if (id > 999999L) {
            String mensaje = "El id del despacho está fuera del rango permitido";
            String detalle = "Valor recibido: " + id;
            throw new IllegalArgumentException(mensaje + ". " + detalle);
        }
    }
}
