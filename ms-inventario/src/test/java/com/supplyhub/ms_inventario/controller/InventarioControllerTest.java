package com.supplyhub.ms_inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyhub.ms_inventario.dto.InventarioRequestDTO;
import com.supplyhub.ms_inventario.dto.InventarioResponseDTO;
import com.supplyhub.ms_inventario.dto.ProductoDTO;
import com.supplyhub.ms_inventario.security.JwtFilter;
import com.supplyhub.ms_inventario.security.JwtUtil;
import com.supplyhub.ms_inventario.service.InventarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventarioService service;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtUtil jwtUtil;

    private InventarioResponseDTO inventarioEjemplo() {
        InventarioResponseDTO dto = new InventarioResponseDTO();
        dto.setId(1L);
        dto.setProducto(new ProductoDTO(1L, "Martillo", "Martillo de acero", 15000, null, null, "ACTIVO"));
        dto.setStockDisponible(100);
        dto.setStockMinimo(10);
        dto.setUbicacion("Bodega A");
        dto.setEstado("DISPONIBLE");
        return dto;
    }

    @Test
    void debeListarInventario() throws Exception {
        when(service.listar(any())).thenReturn(List.of(inventarioEjemplo()));

        mockMvc.perform(get("/api/v1/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Inventario listado"))
                .andExpect(jsonPath("$.data[0].ubicacion").value("Bodega A"));
    }

    @Test
    void debeObtenerInventarioPorId() throws Exception {
        when(service.buscarPorId(eq(1L), any())).thenReturn(inventarioEjemplo());

        mockMvc.perform(get("/api/v1/inventario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Inventario encontrado"))
                .andExpect(jsonPath("$.data.ubicacion").value("Bodega A"));
    }

    @Test
    void debeCrearInventario() throws Exception {
        InventarioRequestDTO dto = new InventarioRequestDTO();
        dto.setIdProducto(1L);
        dto.setStockDisponible(100);
        dto.setStockMinimo(10);
        dto.setUbicacion("Bodega A");
        dto.setEstado("DISPONIBLE");

        when(service.guardar(any(InventarioRequestDTO.class), any())).thenReturn(inventarioEjemplo());

        mockMvc.perform(post("/api/v1/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Inventario creado"))
                .andExpect(jsonPath("$.data.ubicacion").value("Bodega A"));
    }

    @Test
    void debeActualizarInventario() throws Exception {
        InventarioRequestDTO dto = new InventarioRequestDTO();
        dto.setIdProducto(1L);
        dto.setStockDisponible(80);
        dto.setStockMinimo(15);
        dto.setUbicacion("Bodega B");
        dto.setEstado("DISPONIBLE");

        InventarioResponseDTO actualizado = inventarioEjemplo();
        actualizado.setUbicacion("Bodega B");
        when(service.actualizar(eq(1L), any(InventarioRequestDTO.class), any())).thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/inventario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Inventario actualizado"))
                .andExpect(jsonPath("$.data.ubicacion").value("Bodega B"));
    }

    @Test
    void debeEliminarInventario() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/v1/inventario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Inventario eliminado"));
    }
}
