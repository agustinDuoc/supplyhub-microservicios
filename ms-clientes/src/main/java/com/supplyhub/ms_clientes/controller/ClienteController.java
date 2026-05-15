package com.supplyhub.ms_clientes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_clientes.dto.ApiResponse;
import com.supplyhub.ms_clientes.dto.ClienteRequestDTO;
import com.supplyhub.ms_clientes.model.Cliente;
import com.supplyhub.ms_clientes.service.ClienteService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/clientes")
@Slf4j
public class ClienteController {

    private final ClienteService service;

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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/v1/clientes/{} - Buscando cliente", id);

        return ResponseEntity.ok(
                ApiResponse.<Cliente>builder()
                        .success(true)
                        .message("Cliente encontrado")
                        .data(service.buscarPorId(id))
                        .error(null)
                        .build()
        );
    }

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