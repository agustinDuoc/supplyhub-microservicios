package com.supplyhub.ms_clientes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_clientes.dto.ApiResponse;
import com.supplyhub.ms_clientes.dto.ClienteRequestDTO;
import com.supplyhub.ms_clientes.model.Cliente;
import com.supplyhub.ms_clientes.service.ClienteService;

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

@AllArgsConstructor
@Tag(name = "Clientes", description = "Operaciones CRUD de clientes")
@RestController
@RequestMapping("/api/v1/clientes")
@Slf4j
public class ClienteController {

    private final ClienteService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
            @Operation(summary = "Listar clientes", description = "Retorna la lista completa de clientes registrados en el sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Clientes encontrados","data":[{"id":1,"estado":"ACTIVO"}],"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@GetMapping
    public ResponseEntity<ApiResponse<List<Cliente>>> listar() {
        log.info("GET /api/v1/clientes - Listando clientes");

        return ResponseEntity.ok(
                ApiResponse.<List<Cliente>>builder()
                        .success(true)
                        .message("Clientes encontrados")
                        .data(service.listar())
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
            @Operation(summary = "Buscar cliente por ID", description = "Retorna un cliente específico con links HATEOAS")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Cliente encontrado","data":{"id":1,"estado":"ACTIVO"},"error":null,"_links":{"self":{"href":"/api/v1/clientes/1"},"all":{"href":"/api/v1/clientes"}}}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Cliente no encontrado","data":null,"error":"Cliente no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@GetMapping("/{id}")
    public ResponseEntity<EntityModel<ApiResponse<Cliente>>> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/v1/clientes/{} - Buscando cliente", id);

        ApiResponse<Cliente> response = ApiResponse.<Cliente>builder()
                        .success(true)
                        .message("Cliente encontrado")
                        .data(service.buscarPorId(id))
                        .error(null)
                        .build();

        EntityModel<ApiResponse<Cliente>> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(ClienteController.class).buscarPorId(id)).withSelfRel());
        recurso.add(linkTo(methodOn(ClienteController.class).listar()).withRel("all"));
        recurso.add(linkTo(methodOn(ClienteController.class).eliminar(id)).withRel("delete"));

        return ResponseEntity.ok(recurso);
    }

    @PreAuthorize("hasRole('ADMIN')")
            @Operation(summary = "Crear cliente", description = "Crea un nuevo cliente en el sistema. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cliente creado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Cliente creado correctamente","data":{"id":1},"error":null}
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
    public ResponseEntity<ApiResponse<Cliente>> guardar(@Valid @RequestBody ClienteRequestDTO dto) {
        log.info("POST /api/v1/clientes - Creando cliente: {}", dto.getRazonSocial());

        Cliente cliente = service.guardar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Cliente>builder()
                        .success(true)
                        .message("Cliente creado correctamente")
                        .data(cliente)
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
            @Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Cliente actualizado correctamente","data":{"id":1},"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Cliente no encontrado","data":null,"error":"Cliente no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO dto) {

        log.info("PUT /api/v1/clientes/{} - Actualizando cliente", id);

        Cliente cliente = service.actualizar(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<Cliente>builder()
                        .success(true)
                        .message("Cliente actualizado correctamente")
                        .data(cliente)
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
            @Operation(summary = "Eliminar cliente", description = "Elimina un cliente del sistema por ID. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente eliminado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Cliente eliminado correctamente","data":null,"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Cliente no encontrado","data":null,"error":"Cliente no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        log.warn("DELETE /api/v1/clientes/{} - Eliminando cliente", id);

        service.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Cliente eliminado correctamente")
                        .data(null)
                        .error(null)
                        .build()
        );
    }
}
