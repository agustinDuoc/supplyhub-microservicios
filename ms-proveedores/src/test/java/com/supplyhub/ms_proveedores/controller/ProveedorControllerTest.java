package com.supplyhub.ms_proveedores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyhub.ms_proveedores.dto.ProveedorRequestDTO;
import com.supplyhub.ms_proveedores.model.Proveedor;
import com.supplyhub.ms_proveedores.security.JwtFilter;
import com.supplyhub.ms_proveedores.security.JwtUtil;
import com.supplyhub.ms_proveedores.service.ProveedorService;
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

@WebMvcTest(ProveedorController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProveedorService service;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtUtil jwtUtil;

    private Proveedor proveedorEjemplo() {
        return new Proveedor(1L, "76.987.654-3", "Proveedor Test", "proveedor@test.com",
                "+56987654321", "Av. Industrial 50", "ACTIVO");
    }

    @Test
    void debeListarProveedores() throws Exception {
        when(service.listar()).thenReturn(List.of(proveedorEjemplo()));

        mockMvc.perform(get("/api/v1/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Proveedores encontrados"))
                .andExpect(jsonPath("$.data[0].nombre").value("Proveedor Test"));
    }

    @Test
    void debeObtenerProveedorPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(proveedorEjemplo());

        mockMvc.perform(get("/api/v1/proveedores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Proveedor encontrado"))
                .andExpect(jsonPath("$.data.nombre").value("Proveedor Test"));
    }

    @Test
    void debeCrearProveedor() throws Exception {
        ProveedorRequestDTO dto = new ProveedorRequestDTO();
        dto.setRutProveedor("76.987.654-3");
        dto.setNombre("Proveedor Test");
        dto.setEmail("proveedor@test.com");
        dto.setTelefono("+56987654321");
        dto.setDireccion("Av. Industrial 50");
        dto.setEstado("ACTIVO");

        when(service.guardar(any(ProveedorRequestDTO.class))).thenReturn(proveedorEjemplo());

        mockMvc.perform(post("/api/v1/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Proveedor creado correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Proveedor Test"));
    }

    @Test
    void debeActualizarProveedor() throws Exception {
        ProveedorRequestDTO dto = new ProveedorRequestDTO();
        dto.setRutProveedor("76.987.654-3");
        dto.setNombre("Proveedor Actualizado");
        dto.setEmail("actualizado@test.com");
        dto.setTelefono("+56987654321");
        dto.setDireccion("Av. Nueva 100");
        dto.setEstado("ACTIVO");

        Proveedor actualizado = new Proveedor(1L, "76.987.654-3", "Proveedor Actualizado",
                "actualizado@test.com", "+56987654321", "Av. Nueva 100", "ACTIVO");
        when(service.actualizar(eq(1L), any(ProveedorRequestDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/proveedores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Proveedor actualizado correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Proveedor Actualizado"));
    }

    @Test
    void debeEliminarProveedor() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/v1/proveedores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Proveedor eliminado correctamente"));
    }
}
