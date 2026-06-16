package com.supplyhub.ms_cotizaciones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyhub.ms_cotizaciones.dto.CotizacionRequestDTO;
import com.supplyhub.ms_cotizaciones.dto.CotizacionResponseDTO;
import com.supplyhub.ms_cotizaciones.dto.ProductoDTO;
import com.supplyhub.ms_cotizaciones.security.JwtFilter;
import com.supplyhub.ms_cotizaciones.security.JwtUtil;
import com.supplyhub.ms_cotizaciones.service.CotizacionService;
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

@WebMvcTest(CotizacionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CotizacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CotizacionService service;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtUtil jwtUtil;

    private CotizacionResponseDTO cotizacionEjemplo() {
        return CotizacionResponseDTO.builder()
                .id(1L)
                .producto(new ProductoDTO(1L, "Martillo", "Martillo de acero", 15000, null, null, "ACTIVO"))
                .cantidad(2)
                .total(30000)
                .estado("VIGENTE")
                .build();
    }

    @Test
    void debeListarCotizaciones() throws Exception {
        when(service.listar(any())).thenReturn(List.of(cotizacionEjemplo()));

        mockMvc.perform(get("/api/v1/cotizaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cotizaciones encontradas"))
                .andExpect(jsonPath("$.data[0].estado").value("VIGENTE"));
    }

    @Test
    void debeObtenerCotizacionPorId() throws Exception {
        when(service.buscarPorId(eq(1L), any())).thenReturn(cotizacionEjemplo());

        mockMvc.perform(get("/api/v1/cotizaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cotización encontrada"))
                .andExpect(jsonPath("$.data.total").value(30000));
    }

    @Test
    void debeCrearCotizacion() throws Exception {
        CotizacionRequestDTO dto = new CotizacionRequestDTO();
        dto.setIdProducto(1L);
        dto.setCantidad(2);
        dto.setEstado("VIGENTE");

        when(service.guardar(any(CotizacionRequestDTO.class), any())).thenReturn(cotizacionEjemplo());

        mockMvc.perform(post("/api/v1/cotizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cotización creada correctamente"))
                .andExpect(jsonPath("$.data.total").value(30000));
    }

    @Test
    void debeActualizarCotizacion() throws Exception {
        CotizacionRequestDTO dto = new CotizacionRequestDTO();
        dto.setIdProducto(1L);
        dto.setCantidad(3);
        dto.setEstado("ACEPTADA");

        CotizacionResponseDTO actualizada = cotizacionEjemplo();
        actualizada.setCantidad(3);
        actualizada.setEstado("ACEPTADA");
        when(service.actualizar(eq(1L), any(CotizacionRequestDTO.class), any())).thenReturn(actualizada);

        mockMvc.perform(put("/api/v1/cotizaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cotización actualizada correctamente"))
                .andExpect(jsonPath("$.data.estado").value("ACEPTADA"));
    }

    @Test
    void debeEliminarCotizacion() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/v1/cotizaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cotización eliminada correctamente"));
    }
}
