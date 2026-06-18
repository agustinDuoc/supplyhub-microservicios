package com.supplyhub.ms_ordenes_compra.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_ordenes_compra.dto.ApiResponse;
import com.supplyhub.ms_ordenes_compra.dto.OrdenCompraRequestDTO;
import com.supplyhub.ms_ordenes_compra.dto.OrdenCompraResponseDTO;
import com.supplyhub.ms_ordenes_compra.service.OrdenCompraService;

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
@Tag(name = "Órdenes de compra", description = "Operaciones CRUD de órdenes de compra")
@RestController
@RequestMapping("/api/v1/ordenes-compra")
@Slf4j
public class OrdenCompraController {

    private final OrdenCompraService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
            @Operation(summary = "Listar ordencompras", description = "Retorna la lista completa de ordencompras registrados en el sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"OrdenCompras encontrados","data":[{"id":1,"estado":"ACTIVO"}],"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@GetMapping
    public ResponseEntity<ApiResponse<List<OrdenCompraResponseDTO>>> listar(
            @RequestHeader(value = "Authorization", required = false) String token) {

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

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
            @Operation(summary = "Buscar ordencompra por ID", description = "Retorna un ordencompra específico con links HATEOAS")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OrdenCompra encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"OrdenCompra encontrado","data":{"id":1,"estado":"ACTIVO"},"error":null,"_links":{"self":{"href":"/api/v1/ordenes-compra/1"},"all":{"href":"/api/v1/ordenes-compra"}}}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "OrdenCompra no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"OrdenCompra no encontrado","data":null,"error":"OrdenCompra no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@GetMapping("/{id}")
    public ResponseEntity<EntityModel<ApiResponse<OrdenCompraResponseDTO>>> buscarPorId(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        validarId(id);
        log.info("GET /api/v1/ordenes-compra/{} - Buscando orden de compra", id);

        ApiResponse<OrdenCompraResponseDTO> response = ApiResponse.<OrdenCompraResponseDTO>builder()
                        .success(true)
                        .message("Orden de compra encontrada")
                        .data(service.buscarPorId(id, token))
                        .error(null)
                        .build();

        EntityModel<ApiResponse<OrdenCompraResponseDTO>> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(OrdenCompraController.class).buscarPorId(id, null)).withSelfRel());
        recurso.add(linkTo(methodOn(OrdenCompraController.class).listar(null)).withRel("all"));
        recurso.add(linkTo(methodOn(OrdenCompraController.class).actualizar(id, null, null)).withRel("update"));
        recurso.add(linkTo(methodOn(OrdenCompraController.class).eliminar(id)).withRel("delete"));

        return ResponseEntity.ok(recurso);
    }

    @PreAuthorize("hasRole('ADMIN')")
            @Operation(summary = "Crear ordencompra", description = "Crea un nuevo ordencompra en el sistema. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "OrdenCompra creado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"OrdenCompra creado correctamente","data":{"id":1},"error":null}
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
    public ResponseEntity<ApiResponse<OrdenCompraResponseDTO>> guardar(
            @Valid @RequestBody OrdenCompraRequestDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {

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

    @PreAuthorize("hasRole('ADMIN')")
            @Operation(summary = "Actualizar ordencompra", description = "Actualiza los datos de un ordencompra existente. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OrdenCompra actualizado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"OrdenCompra actualizado correctamente","data":{"id":1},"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "OrdenCompra no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"OrdenCompra no encontrado","data":null,"error":"OrdenCompra no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrdenCompraResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody OrdenCompraRequestDTO dto,
            @RequestHeader(value = "Authorization", required = false) String token) {

        validarId(id);
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

    @PreAuthorize("hasRole('ADMIN')")
            @Operation(summary = "Eliminar ordencompra", description = "Elimina un ordencompra del sistema por ID. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OrdenCompra eliminado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"OrdenCompra eliminado correctamente","data":null,"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "OrdenCompra no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"OrdenCompra no encontrado","data":null,"error":"OrdenCompra no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {

        validarId(id);
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

    private void validarId(Long id) {
        if (id == null) {
            String mensaje = "El id de la orden de compra es obligatorio";
            throw new IllegalArgumentException(mensaje);
        }
        if (id <= 0) {
            String mensaje = "El id de la orden de compra debe ser positivo";
            throw new IllegalArgumentException(mensaje);
        }
        if (id > 999999L) {
            String mensaje = "El id de la orden de compra está fuera del rango permitido";
            String detalle = "Valor recibido: " + id;
            throw new IllegalArgumentException(mensaje + ". " + detalle);
        }
    }
}
