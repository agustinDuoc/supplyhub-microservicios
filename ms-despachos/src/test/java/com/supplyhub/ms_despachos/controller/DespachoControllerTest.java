package com.supplyhub.ms_despachos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyhub.ms_despachos.dto.DespachoRequestDTO;
import com.supplyhub.ms_despachos.dto.DespachoResponseDTO;
import com.supplyhub.ms_despachos.dto.OrdenCompraDTO;
import com.supplyhub.ms_despachos.dto.PagoDTO;
import com.supplyhub.ms_despachos.security.JwtFilter;
import com.supplyhub.ms_despachos.security.JwtUtil;
import com.supplyhub.ms_despachos.service.DespachoService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DespachoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DespachoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DespachoService service;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtUtil jwtUtil;

    private DespachoResponseDTO despachoEjemplo() {
        OrdenCompraDTO orden = new OrdenCompraDTO(1L, null, null, 2, 30000, "APROBADA", LocalDate.now());
        PagoDTO pago = new PagoDTO(1L, Map.of("id", 1L), 30000, "TRANSFERENCIA", "APROBADO", LocalDate.now());
        return DespachoResponseDTO.builder()
                .id(1L)
                .ordenCompra(orden)
                .pago(pago)
                .direccionEnvio("Av. Entrega 123")
                .estadoDespacho("EN_PREPARACION")
                .fechaEnvio(LocalDate.now())
                .fechaEntrega(LocalDate.now().plusDays(3))
                .build();
    }

    @Test
    void debeListarDespachos() throws Exception {
        when(service.listar(any())).thenReturn(List.of(despachoEjemplo()));

        mockMvc.perform(get("/api/v1/despachos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Despachos encontrados"))
                .andExpect(jsonPath("$.data[0].direccionEnvio").value("Av. Entrega 123"));
    }

    @Test
    void debeObtenerDespachoPorId() throws Exception {
        when(service.buscarPorId(eq(1L), any())).thenReturn(despachoEjemplo());

        mockMvc.perform(get("/api/v1/despachos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Despacho encontrado"))
                .andExpect(jsonPath("$.data.estadoDespacho").value("EN_PREPARACION"));
    }

    @Test
    void debeCrearDespacho() throws Exception {
        DespachoRequestDTO dto = new DespachoRequestDTO();
        dto.setIdOrdenCompra(1L);
        dto.setIdPago(1L);
        dto.setDireccionEnvio("Av. Entrega 123");
        dto.setEstadoDespacho("EN_PREPARACION");
        dto.setFechaEntrega(LocalDate.now().plusDays(3));

        when(service.guardar(any(DespachoRequestDTO.class), any())).thenReturn(despachoEjemplo());

        mockMvc.perform(post("/api/v1/despachos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Despacho creado correctamente"))
                .andExpect(jsonPath("$.data.direccionEnvio").value("Av. Entrega 123"));
    }

    @Test
    void debeActualizarDespacho() throws Exception {
        DespachoRequestDTO dto = new DespachoRequestDTO();
        dto.setIdOrdenCompra(1L);
        dto.setIdPago(1L);
        dto.setDireccionEnvio("Av. Nueva 456");
        dto.setEstadoDespacho("ENVIADO");
        dto.setFechaEntrega(LocalDate.now().plusDays(2));

        DespachoResponseDTO actualizado = despachoEjemplo();
        actualizado.setEstadoDespacho("ENVIADO");
        actualizado.setDireccionEnvio("Av. Nueva 456");
        when(service.actualizar(eq(1L), any(DespachoRequestDTO.class), any())).thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/despachos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Despacho actualizado correctamente"))
                .andExpect(jsonPath("$.data.estadoDespacho").value("ENVIADO"));
    }

    @Test
    void debeEliminarDespacho() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/v1/despachos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Despacho eliminado correctamente"));
    }
}
