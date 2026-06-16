package com.supplyhub.ms_inventario.service;

import com.supplyhub.ms_inventario.client.ProductoClient;
import com.supplyhub.ms_inventario.dto.InventarioRequestDTO;
import com.supplyhub.ms_inventario.dto.InventarioResponseDTO;
import com.supplyhub.ms_inventario.dto.ProductoDTO;
import com.supplyhub.ms_inventario.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_inventario.model.Inventario;
import com.supplyhub.ms_inventario.repository.InventarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository repository;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private InventarioService service;

    private ProductoDTO productoActivo() {
        return new ProductoDTO(1L, "Martillo", "Martillo de acero", 15000, null, null, "ACTIVO");
    }

    private InventarioRequestDTO dtoValido() {
        InventarioRequestDTO dto = new InventarioRequestDTO();
        dto.setIdProducto(1L);
        dto.setStockDisponible(100);
        dto.setStockMinimo(10);
        dto.setUbicacion("Bodega A");
        dto.setEstado("DISPONIBLE");
        return dto;
    }

    @Test
    void deberiaRetornarInventarioCuandoExiste() {
        Inventario inv = new Inventario(1L, 1L, 100, 10, "Bodega A", "DISPONIBLE");
        when(repository.findById(1L)).thenReturn(Optional.of(inv));
        when(productoClient.obtenerProducto(1L, null)).thenReturn(productoActivo());

        InventarioResponseDTO resultado = service.buscarPorId(1L, null);

        assertNotNull(resultado);
        assertEquals("Bodega A", resultado.getUbicacion());
        verify(repository).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoInventarioNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.buscarPorId(99L, null));
        verify(repository).findById(99L);
    }

    @Test
    void deberiaRetornarListaInventario() {
        Inventario inv = new Inventario(1L, 1L, 100, 10, "Bodega A", "DISPONIBLE");
        when(repository.findAll()).thenReturn(List.of(inv));
        when(productoClient.obtenerProducto(1L, null)).thenReturn(productoActivo());

        List<InventarioResponseDTO> resultado = service.listar(null);

        assertEquals(1, resultado.size());
        assertEquals("Bodega A", resultado.get(0).getUbicacion());
        verify(repository).findAll();
    }

    @Test
    void deberiaCrearInventarioCorrectamente() {
        InventarioRequestDTO dto = dtoValido();
        when(productoClient.obtenerProducto(1L, null)).thenReturn(productoActivo());
        when(repository.findByIdProducto(1L)).thenReturn(Optional.empty());
        when(repository.save(any(Inventario.class))).thenAnswer(inv -> {
            Inventario i = inv.getArgument(0);
            i.setId(1L);
            return i;
        });

        InventarioResponseDTO resultado = service.guardar(dto, null);

        assertEquals(1L, resultado.getId());
        assertEquals("DISPONIBLE", resultado.getEstado());
        verify(repository).save(any(Inventario.class));
    }

    @Test
    void deberiaActualizarInventarioCorrectamente() {
        Inventario existente = new Inventario(1L, 1L, 50, 10, "Bodega A", "DISPONIBLE");
        InventarioRequestDTO dto = dtoValido();
        dto.setUbicacion("Bodega B");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoClient.obtenerProducto(1L, null)).thenReturn(productoActivo());
        when(repository.findByIdProducto(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Inventario.class))).thenAnswer(inv -> inv.getArgument(0));

        InventarioResponseDTO resultado = service.actualizar(1L, dto, null);

        assertEquals("Bodega B", resultado.getUbicacion());
        verify(repository).findById(1L);
        verify(repository).save(existente);
    }

    @Test
    void deberiaEliminarInventarioPorId() {
        Inventario inv = new Inventario(1L, 1L, 100, 10, "Bodega A", "DISPONIBLE");
        when(repository.findById(1L)).thenReturn(Optional.of(inv));

        service.eliminar(1L);

        verify(repository).findById(1L);
        verify(repository).delete(inv);
    }

    @Test
    void deberiaCalcularEstadoSinStock() {
        InventarioRequestDTO dto = dtoValido();
        dto.setStockDisponible(0);
        dto.setStockMinimo(10);

        when(productoClient.obtenerProducto(1L, null)).thenReturn(productoActivo());
        when(repository.findByIdProducto(1L)).thenReturn(Optional.empty());
        when(repository.save(any(Inventario.class))).thenAnswer(inv -> {
            Inventario i = inv.getArgument(0);
            i.setId(1L);
            return i;
        });

        InventarioResponseDTO resultado = service.guardar(dto, null);

        assertEquals("SIN_STOCK", resultado.getEstado());
    }
}
