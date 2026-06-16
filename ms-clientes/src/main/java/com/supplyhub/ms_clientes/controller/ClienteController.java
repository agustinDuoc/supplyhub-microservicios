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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@AllArgsConstructor
@Tag(name = "Clientes", description = "Operaciones CRUD de clientes")
@RestController
@RequestMapping("/api/v1/clientes")
@Slf4j
public class ClienteController {

    private final ClienteService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
        @Operation(summary = "Listar recursos", description = "Endpoint para listar recursos")
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
        @Operation(summary = "Buscar recurso por ID", description = "Endpoint para buscar recurso por id")
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
        @Operation(summary = "Crear recurso", description = "Endpoint para crear recurso")
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
        @Operation(summary = "Actualizar recurso", description = "Endpoint para actualizar recurso")
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
        @Operation(summary = "Eliminar recurso", description = "Endpoint para eliminar recurso")
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
