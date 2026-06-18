package com.supplyhub.ms_categorias.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.supplyhub.ms_categorias.dto.ApiResponse;
import com.supplyhub.ms_categorias.dto.CategoriaRequestDTO;
import com.supplyhub.ms_categorias.model.Categoria;
import com.supplyhub.ms_categorias.service.CategoriaService;

import lombok.extern.slf4j.Slf4j;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@AllArgsConstructor
@Tag(name = "Categorías", description = "Operaciones CRUD de categorías")
@RestController
@RequestMapping("/api/v1/categorias")
@Slf4j
public class CategoriaController {

    private final CategoriaService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
            @Operation(summary = "Listar categorias", description = "Retorna la lista completa de categorias registrados en el sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Categorias encontrados","data":[{"id":1,"estado":"ACTIVO"}],"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@GetMapping
    public ResponseEntity<ApiResponse<List<Categoria>>> listar() {
        log.info("GET /api/v1/categorias - Listado categorias");
        return ResponseEntity.ok(
                ApiResponse.<List<Categoria>>builder()
                        .success(true)
                        .message("Categorías encontradas")
                        .data(service.listar())
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
            @Operation(summary = "Buscar categoria por ID", description = "Retorna un categoria específico con links HATEOAS")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categoria encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Categoria encontrado","data":{"id":1,"estado":"ACTIVO"},"error":null,"_links":{"self":{"href":"/api/v1/categorias/1"},"all":{"href":"/api/v1/categorias"}}}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoria no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Categoria no encontrado","data":null,"error":"Categoria no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@GetMapping("/{id}")
    public ResponseEntity<EntityModel<ApiResponse<Categoria>>> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/v1/categorias/{} - Buscando categoria", id);
        ApiResponse<Categoria> response = ApiResponse.<Categoria>builder()
                        .success(true)
                        .message("Categoría encontrada")
                        .data(service.buscarPorId(id))
                        .error(null)
                        .build();

        EntityModel<ApiResponse<Categoria>> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(CategoriaController.class).buscarPorId(id)).withSelfRel());
        recurso.add(linkTo(methodOn(CategoriaController.class).listar()).withRel("all"));
        recurso.add(linkTo(methodOn(CategoriaController.class).eliminar(id)).withRel("delete"));

        return ResponseEntity.ok(recurso);
    }

    @PreAuthorize("hasRole('ADMIN')")
            @Operation(summary = "Crear categoria", description = "Crea un nuevo categoria en el sistema. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Categoria creado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Categoria creado correctamente","data":{"id":1},"error":null}
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
    public ResponseEntity<ApiResponse<Categoria>> guardar(@Valid @RequestBody CategoriaRequestDTO dto) {
        log.info("POST /api/v1/categorias - Creando categoría: {}", dto.getNombre());
        Categoria categoria = service.guardar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Categoria>builder()
                        .success(true)
                        .message("Categoría creada correctamente")
                        .data(categoria)
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
            @Operation(summary = "Actualizar categoria", description = "Actualiza los datos de un categoria existente. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categoria actualizado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Categoria actualizado correctamente","data":{"id":1},"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoria no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Categoria no encontrado","data":null,"error":"Categoria no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Categoria>> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO dto) {
        log.info("PUT /api/v1/categorias/{} - Actualizando categoría", id);
        Categoria categoria = service.actualizar(id, dto);
        return ResponseEntity.ok(
                ApiResponse.<Categoria>builder()
                        .success(true)
                        .message("Categoría actualizada correctamente")
                        .data(categoria)
                        .error(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
            @Operation(summary = "Eliminar categoria", description = "Elimina un categoria del sistema por ID. Requiere rol ADMIN")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categoria eliminado exitosamente",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":true,"message":"Categoria eliminado correctamente","data":null,"error":null}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoria no encontrado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"success":false,"message":"Categoria no encontrado","data":null,"error":"Categoria no encontrada con id: 99"}
                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"timestamp":"2026-06-16","status":401,"error":"Unauthorized","message":"Full authentication is required"}
                """)))
    })
@DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        log.warn("DELETE /api/v1/categorias/{} - Eliminando categoría", id);
        service.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Categoría eliminada correctamente")
                        .data(null)
                        .error(null)
                        .build()
        );
    }
}
