package com.supplyhub.ms_ordenes_compra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyhub.ms_ordenes_compra.dto.ClienteDTO;
import com.supplyhub.ms_ordenes_compra.dto.InventarioDTO;
import com.supplyhub.ms_ordenes_compra.dto.OrdenCompraRequestDTO;
import com.supplyhub.ms_ordenes_compra.dto.OrdenCompraResponseDTO;
import com.supplyhub.ms_ordenes_compra.security.JwtFilter;
import com.supplyhub.ms_ordenes_compra.security.JwtUtil;
import com.supplyhub.ms_ordenes_compra.service.OrdenCompraService;
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

@WebMvcTest(OrdenCompraController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OrdenCompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrdenCompraService service;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtUtil jwtUtil;

    private OrdenCompraResponseDTO ordenEjemplo() {
        return OrdenCompraResponseDTO.builder()
                .id(1L)
                .cliente(new ClienteDTO(1L, "76.123.456-7", "Empresa Test", "test@empresa.com",
                        "+56912345678", "Av. Principal", "ACTIVO"))
                .inventario(new InventarioDTO(1L, Map.of("precio", 15000), 100, 10, "Bodega A", "DISPONIBLE"))
                .cantidad(2)
                .total(30000)
                .estado("PENDIENTE")
                .fechaOrden(LocalDate.now())
                .build();
    }

    @Test
    void debeListarOrdenesCompra() throws Exception {
        when(service.listar(any())).thenReturn(List.of(ordenEjemplo()));

        mockMvc.perform(get("/api/v1/ordenes-compra"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Órdenes de compra encontradas"))
                .andExpect(jsonPath("$.data[0].estado").value("PENDIENTE"));
    }

    @Test
    void debeObtenerOrdenCompraPorId() throws Exception {
        when(service.buscarPorId(eq(1L), any())).thenReturn(ordenEjemplo());

        mockMvc.perform(get("/api/v1/ordenes-compra/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Orden de compra encontrada"))
                .andExpect(jsonPath("$.data.total").value(30000));
    }

    @Test
    void debeCrearOrdenCompra() throws Exception {
        OrdenCompraRequestDTO dto = new OrdenCompraRequestDTO();
        dto.setIdCliente(1L);
        dto.setIdInventario(1L);
        dto.setCantidad(2);
        dto.setEstado("PENDIENTE");

        when(service.guardar(any(OrdenCompraRequestDTO.class), any())).thenReturn(ordenEjemplo());

        mockMvc.perform(post("/api/v1/ordenes-compra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Orden de compra creada correctamente"))
                .andExpect(jsonPath("$.data.total").value(30000));
    }

    @Test
    void debeActualizarOrdenCompra() throws Exception {
        OrdenCompraRequestDTO dto = new OrdenCompraRequestDTO();
        dto.setIdCliente(1L);
        dto.setIdInventario(1L);
        dto.setCantidad(3);
        dto.setEstado("APROBADA");

        OrdenCompraResponseDTO actualizada = ordenEjemplo();
        actualizada.setEstado("APROBADA");
        when(service.actualizar(eq(1L), any(OrdenCompraRequestDTO.class), any())).thenReturn(actualizada);

        mockMvc.perform(put("/api/v1/ordenes-compra/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Orden de compra actualizada correctamente"))
                .andExpect(jsonPath("$.data.estado").value("APROBADA"));
    }

    @Test
    void debeEliminarOrdenCompra() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/v1/ordenes-compra/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Orden de compra eliminada correctamente"));
    }
}
