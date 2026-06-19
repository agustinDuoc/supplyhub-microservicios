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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Inventario", description = "Operaciones CRUD de inventario")
@RestController
@RequestMapping("/api/v1/inventario")
@AllArgsConstructor
public class InventarioController {

    private final InventarioService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
            @Operation(summary = "Listar inventarios", description = "Retorna la lista completa de inventarios registrados en el sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Inventarios encontrados","data":[{"id":1,"estado":"ACTIVO"}],"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
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
            @Operation(summary = "Crear inventario", description = "Crea un nuevo inventario en el sistema. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Inventario creado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Inventario creado correctamente","data":{"id":1},"error":null}
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
            @Operation(summary = "Buscar inventario por ID", description = "Retorna un inventario específico con links HATEOAS")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inventario encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Inventario encontrado","data":{"id":1,"estado":"ACTIVO"},"error":null,"_links":{"self":{"href":"/api/v1/inventario/1"},"all":{"href":"/api/v1/inventario"}}}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Inventario no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Inventario no encontrado","data":null,"error":"Inventario no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@GetMapping("/{id}")
    public ResponseEntity<EntityModel<ApiResponse<InventarioResponseDTO>>> buscarPorId(@PathVariable Long id,
                                                                          @RequestHeader(value = "Authorization", required = false) String token) {
        validarId(id);
        ApiResponse<InventarioResponseDTO> response = ApiResponse.<InventarioResponseDTO>builder()
                        .success(true)
                        .message("Inventario encontrado")
                        .data(service.buscarPorId(id, token))
                        .error(null)
                        .build();

        EntityModel<ApiResponse<InventarioResponseDTO>> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(InventarioController.class).buscarPorId(id, null)).withSelfRel());
        recurso.add(linkTo(methodOn(InventarioController.class).listar(null)).withRel("all"));
        recurso.add(linkTo(methodOn(InventarioController.class).actualizar(id, null, null)).withRel("update"));
        recurso.add(linkTo(methodOn(InventarioController.class).eliminar(id)).withRel("delete"));

        return ResponseEntity.ok(recurso);
    }

    @PreAuthorize("hasRole('ADMIN')")
            @Operation(summary = "Actualizar inventario", description = "Actualiza los datos de un inventario existente. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inventario actualizado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Inventario actualizado correctamente","data":{"id":1},"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Inventario no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Inventario no encontrado","data":null,"error":"Inventario no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> actualizar(@PathVariable Long id,
                                                                         @Valid @RequestBody InventarioRequestDTO dto,
                                                                         @RequestHeader(value = "Authorization", required = false) String token) {
        validarId(id);
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
            @Operation(summary = "Eliminar inventario", description = "Elimina un inventario del sistema por ID. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inventario eliminado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Inventario eliminado correctamente","data":null,"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Inventario no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Inventario no encontrado","data":null,"error":"Inventario no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        validarId(id);
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

    private void validarId(Long id) {
        if (id == null) {
            String mensaje = "El id del inventario es obligatorio";
            throw new IllegalArgumentException(mensaje);
        }
        if (id <= 0) {
            String mensaje = "El id del inventario debe ser positivo";
            throw new IllegalArgumentException(mensaje);
        }
        if (id > 999999L) {
            String mensaje = "El id del inventario está fuera del rango permitido";
            throw new IllegalArgumentException(mensaje);
        }
    }
}
