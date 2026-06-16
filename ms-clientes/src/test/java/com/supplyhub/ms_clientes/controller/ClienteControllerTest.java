package com.supplyhub.ms_clientes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyhub.ms_clientes.dto.ClienteRequestDTO;
import com.supplyhub.ms_clientes.model.Cliente;
import com.supplyhub.ms_clientes.security.JwtFilter;
import com.supplyhub.ms_clientes.security.JwtUtil;
import com.supplyhub.ms_clientes.service.ClienteService;
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

@WebMvcTest(ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService service;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtUtil jwtUtil;

    private Cliente clienteEjemplo() {
        return new Cliente(1L, "76.123.456-7", "Empresa Test", "test@empresa.com",
                "+56912345678", "Av. Principal 100", "ACTIVO");
    }

    @Test
    void debeListarClientes() throws Exception {
        when(service.listar()).thenReturn(List.of(clienteEjemplo()));

        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Clientes encontrados"))
                .andExpect(jsonPath("$.data[0].razonSocial").value("Empresa Test"));
    }

    @Test
    void debeObtenerClientePorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(clienteEjemplo());

        mockMvc.perform(get("/api/v1/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cliente encontrado"))
                .andExpect(jsonPath("$.data.razonSocial").value("Empresa Test"));
    }

    @Test
    void debeCrearCliente() throws Exception {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setRutEmpresa("76.123.456-7");
        dto.setRazonSocial("Empresa Test");
        dto.setEmail("test@empresa.com");
        dto.setTelefono("+56912345678");
        dto.setDireccion("Av. Principal 100");
        dto.setEstado("ACTIVO");

        when(service.guardar(any(ClienteRequestDTO.class))).thenReturn(clienteEjemplo());

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cliente creado correctamente"))
                .andExpect(jsonPath("$.data.razonSocial").value("Empresa Test"));
    }

    @Test
    void debeActualizarCliente() throws Exception {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setRutEmpresa("76.123.456-7");
        dto.setRazonSocial("Empresa Actualizada");
        dto.setEmail("actualizada@empresa.com");
        dto.setTelefono("+56912345678");
        dto.setDireccion("Av. Nueva 200");
        dto.setEstado("ACTIVO");

        Cliente actualizado = new Cliente(1L, "76.123.456-7", "Empresa Actualizada",
                "actualizada@empresa.com", "+56912345678", "Av. Nueva 200", "ACTIVO");
        when(service.actualizar(eq(1L), any(ClienteRequestDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cliente actualizado correctamente"))
                .andExpect(jsonPath("$.data.razonSocial").value("Empresa Actualizada"));
    }

    @Test
    void debeEliminarCliente() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/v1/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cliente eliminado correctamente"));
    }
}
