package com.supplyhub.ms_categorias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyhub.ms_categorias.dto.CategoriaRequestDTO;
import com.supplyhub.ms_categorias.model.Categoria;
import com.supplyhub.ms_categorias.security.JwtFilter;
import com.supplyhub.ms_categorias.security.JwtUtil;
import com.supplyhub.ms_categorias.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaService service;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void debeListarCategorias() throws Exception {
        when(service.listar()).thenReturn(List.of(
                new Categoria(1L, "Seguridad", "Productos de seguridad", "ACTIVO")
        ));

        mockMvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Categorías encontradas"))
                .andExpect(jsonPath("$.data[0].nombre").value("Seguridad"));
    }

    @Test
    void debeObtenerCategoriaPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(
                new Categoria(1L, "Herramientas", "Herramientas manuales", "ACTIVO")
        );

        mockMvc.perform(get("/api/v1/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Categoría encontrada"))
                .andExpect(jsonPath("$.data.nombre").value("Herramientas"));
    }

    @Test
    void debeCrearCategoria() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Repuestos");
        dto.setDescripcion("Piezas industriales");
        dto.setEstado("ACTIVO");

        when(service.guardar(any(CategoriaRequestDTO.class)))
                .thenReturn(new Categoria(1L, "Repuestos", "Piezas industriales", "ACTIVO"));

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Categoría creada correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Repuestos"));
    }

    @Test
    void debeActualizarCategoria() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Actualizada");
        dto.setDescripcion("Nueva descripcion");
        dto.setEstado("ACTIVO");

        when(service.actualizar(eq(1L), any(CategoriaRequestDTO.class)))
                .thenReturn(new Categoria(1L, "Actualizada", "Nueva descripcion", "ACTIVO"));

        mockMvc.perform(put("/api/v1/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Categoría actualizada correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Actualizada"));
    }

    @Test
    void debeEliminarCategoria() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/v1/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Categoría eliminada correctamente"));
    }
}
