package com.supplyhub.ms_pagos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyhub.ms_pagos.dto.OrdenCompraDTO;
import com.supplyhub.ms_pagos.dto.PagoRequestDTO;
import com.supplyhub.ms_pagos.dto.PagoResponseDTO;
import com.supplyhub.ms_pagos.security.JwtFilter;
import com.supplyhub.ms_pagos.security.JwtUtil;
import com.supplyhub.ms_pagos.service.PagoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PagoService service;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtUtil jwtUtil;

    private PagoResponseDTO pagoEjemplo() {
        OrdenCompraDTO orden = new OrdenCompraDTO(1L, null, null, 2, 30000, "PENDIENTE", LocalDate.now());
        return PagoResponseDTO.builder()
                .id(1L)
                .ordenCompra(orden)
                .monto(30000)
                .metodoPago("TRANSFERENCIA")
                .estadoPago("APROBADO")
                .fechaPago(LocalDate.now())
                .build();
    }

    @Test
    void debeListarPagos() throws Exception {
        when(service.listar(any())).thenReturn(List.of(pagoEjemplo()));

        mockMvc.perform(get("/api/v1/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Pagos encontrados"))
                .andExpect(jsonPath("$.data[0].metodoPago").value("TRANSFERENCIA"));
    }

    @Test
    void debeObtenerPagoPorId() throws Exception {
        when(service.buscarPorId(eq(1L), any())).thenReturn(pagoEjemplo());

        mockMvc.perform(get("/api/v1/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Pago encontrado"))
                .andExpect(jsonPath("$.data.monto").value(30000));
    }

    @Test
    void debeCrearPago() throws Exception {
        PagoRequestDTO dto = new PagoRequestDTO();
        dto.setIdOrdenCompra(1L);
        dto.setMonto(30000);
        dto.setMetodoPago("TRANSFERENCIA");
        dto.setEstadoPago("APROBADO");

        when(service.guardar(any(PagoRequestDTO.class), any())).thenReturn(pagoEjemplo());

        mockMvc.perform(post("/api/v1/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Pago creado correctamente"))
                .andExpect(jsonPath("$.data.monto").value(30000));
    }

    @Test
    void debeActualizarPago() throws Exception {
        PagoRequestDTO dto = new PagoRequestDTO();
        dto.setIdOrdenCompra(1L);
        dto.setMonto(35000);
        dto.setMetodoPago("TARJETA");
        dto.setEstadoPago("PENDIENTE");

        PagoResponseDTO actualizado = pagoEjemplo();
        actualizado.setMetodoPago("TARJETA");
        actualizado.setEstadoPago("PENDIENTE");
        when(service.actualizar(eq(1L), any(PagoRequestDTO.class), any())).thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/pagos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Pago actualizado correctamente"))
                .andExpect(jsonPath("$.data.metodoPago").value("TARJETA"));
    }

    @Test
    void debeEliminarPago() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/v1/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Pago eliminado correctamente"));
    }
}
