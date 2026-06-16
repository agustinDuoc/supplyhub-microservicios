package com.supplyhub.ms_productos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyhub.ms_productos.dto.CategoriaDTO;
import com.supplyhub.ms_productos.dto.ProductoRequestDTO;
import com.supplyhub.ms_productos.dto.ProductoResponseDTO;
import com.supplyhub.ms_productos.dto.ProveedorDTO;
import com.supplyhub.ms_productos.security.JwtFilter;
import com.supplyhub.ms_productos.security.JwtUtil;
import com.supplyhub.ms_productos.service.ProductoService;
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

@WebMvcTest(ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService service;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtUtil jwtUtil;

    private ProductoResponseDTO productoEjemplo() {
        return ProductoResponseDTO.builder()
                .id(1L)
                .nombre("Martillo")
                .descripcion("Martillo de acero")
                .precio(15000)
                .categoria(new CategoriaDTO(1L, "Herramientas", "Herramientas manuales", "ACTIVO"))
                .proveedor(new ProveedorDTO(1L, "76.987.654-3", "Proveedor Test", "prov@test.com",
                        "+56987654321", "Av. Industrial", "ACTIVO"))
                .estado("ACTIVO")
                .build();
    }

    @Test
    void debeListarProductos() throws Exception {
        when(service.listar(any())).thenReturn(List.of(productoEjemplo()));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Productos encontrados"))
                .andExpect(jsonPath("$.data[0].nombre").value("Martillo"));
    }

    @Test
    void debeObtenerProductoPorId() throws Exception {
        when(service.buscarPorId(eq(1L), any())).thenReturn(productoEjemplo());

        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Producto encontrado"))
                .andExpect(jsonPath("$.data.nombre").value("Martillo"));
    }

    @Test
    void debeCrearProducto() throws Exception {
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre("Martillo");
        dto.setDescripcion("Martillo de acero");
        dto.setPrecio(15000);
        dto.setIdCategoria(1L);
        dto.setIdProveedor(1L);
        dto.setEstado("ACTIVO");

        when(service.guardar(any(ProductoRequestDTO.class), any())).thenReturn(productoEjemplo());

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Producto creado correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Martillo"));
    }

    @Test
    void debeActualizarProducto() throws Exception {
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre("Martillo Pro");
        dto.setDescripcion("Martillo profesional");
        dto.setPrecio(20000);
        dto.setIdCategoria(1L);
        dto.setIdProveedor(1L);
        dto.setEstado("ACTIVO");

        ProductoResponseDTO actualizado = productoEjemplo();
        actualizado.setNombre("Martillo Pro");
        when(service.actualizar(eq(1L), any(ProductoRequestDTO.class), any())).thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Producto actualizado correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Martillo Pro"));
    }

    @Test
    void debeEliminarProducto() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Producto eliminado correctamente"));
    }
}
