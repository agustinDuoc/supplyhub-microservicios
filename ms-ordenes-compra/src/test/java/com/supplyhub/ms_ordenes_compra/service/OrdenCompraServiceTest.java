package com.supplyhub.ms_ordenes_compra.service;

import com.supplyhub.ms_ordenes_compra.client.ClienteClient;
import com.supplyhub.ms_ordenes_compra.client.InventarioClient;
import com.supplyhub.ms_ordenes_compra.dto.ClienteDTO;
import com.supplyhub.ms_ordenes_compra.dto.InventarioDTO;
import com.supplyhub.ms_ordenes_compra.dto.OrdenCompraRequestDTO;
import com.supplyhub.ms_ordenes_compra.dto.OrdenCompraResponseDTO;
import com.supplyhub.ms_ordenes_compra.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_ordenes_compra.model.OrdenCompra;
import com.supplyhub.ms_ordenes_compra.repository.OrdenCompraRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenCompraServiceTest {

    @Mock
    private OrdenCompraRepository repository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private OrdenCompraService service;

    private ClienteDTO clienteActivo() {
        return new ClienteDTO(1L, "76.123.456-7", "Empresa Test", "test@empresa.com",
                "+56912345678", "Av. Principal", "ACTIVO");
    }

    private InventarioDTO inventarioDisponible() {
        return new InventarioDTO(1L, Map.of("precio", 15000), 100, 10, "Bodega A", "DISPONIBLE");
    }

    private OrdenCompraRequestDTO dtoValido() {
        OrdenCompraRequestDTO dto = new OrdenCompraRequestDTO();
        dto.setIdCliente(1L);
        dto.setIdInventario(1L);
        dto.setCantidad(2);
        dto.setEstado("PENDIENTE");
        return dto;
    }

    @Test
    void deberiaRetornarOrdenCuandoExiste() {
        OrdenCompra orden = new OrdenCompra(1L, 1L, 1L, 2, 30000, "PENDIENTE", LocalDate.now());
        when(repository.findById(1L)).thenReturn(Optional.of(orden));
        when(clienteClient.obtenerCliente(1L, null)).thenReturn(clienteActivo());
        when(inventarioClient.obtenerInventario(1L, null)).thenReturn(inventarioDisponible());

        OrdenCompraResponseDTO resultado = service.buscarPorId(1L, null);

        assertNotNull(resultado);
        assertEquals(30000, resultado.getTotal());
        verify(repository).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoOrdenNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.buscarPorId(99L, null));
        verify(repository).findById(99L);
    }

    @Test
    void deberiaRetornarListaOrdenes() {
        OrdenCompra orden = new OrdenCompra(1L, 1L, 1L, 2, 30000, "PENDIENTE", LocalDate.now());
        when(repository.findAll()).thenReturn(List.of(orden));
        when(clienteClient.obtenerCliente(1L, null)).thenReturn(clienteActivo());
        when(inventarioClient.obtenerInventario(1L, null)).thenReturn(inventarioDisponible());

        List<OrdenCompraResponseDTO> resultado = service.listar(null);

        assertEquals(1, resultado.size());
        assertEquals("PENDIENTE", resultado.get(0).getEstado());
        verify(repository).findAll();
    }

    @Test
    void deberiaCrearOrdenCorrectamente() {
        OrdenCompraRequestDTO dto = dtoValido();
        when(clienteClient.obtenerCliente(1L, null)).thenReturn(clienteActivo());
        when(inventarioClient.obtenerInventario(1L, null)).thenReturn(inventarioDisponible());
        when(repository.save(any(OrdenCompra.class))).thenAnswer(inv -> {
            OrdenCompra o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        OrdenCompraResponseDTO resultado = service.guardar(dto, null);

        assertEquals(1L, resultado.getId());
        assertEquals(30000, resultado.getTotal());
        verify(repository).save(any(OrdenCompra.class));
    }

    @Test
    void deberiaRechazarClienteInactivo() {
        OrdenCompraRequestDTO dto = dtoValido();
        ClienteDTO inactivo = clienteActivo();
        inactivo.setEstado("INACTIVO");
        when(clienteClient.obtenerCliente(1L, null)).thenReturn(inactivo);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));

        assertTrue(ex.getMessage().contains("cliente inactivo"));
        verify(repository, never()).save(any(OrdenCompra.class));
    }

    @Test
    void deberiaRechazarInventarioSinStock() {
        OrdenCompraRequestDTO dto = dtoValido();
        InventarioDTO sinStock = inventarioDisponible();
        sinStock.setEstado("SIN_STOCK");

        when(clienteClient.obtenerCliente(1L, null)).thenReturn(clienteActivo());
        when(inventarioClient.obtenerInventario(1L, null)).thenReturn(sinStock);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));

        assertTrue(ex.getMessage().contains("inventario sin stock"));
        verify(repository, never()).save(any(OrdenCompra.class));
    }

    @Test
    void deberiaRechazarStockInsuficiente() {
        OrdenCompraRequestDTO dto = dtoValido();
        dto.setCantidad(200);

        when(clienteClient.obtenerCliente(1L, null)).thenReturn(clienteActivo());
        when(inventarioClient.obtenerInventario(1L, null)).thenReturn(inventarioDisponible());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));

        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        verify(repository, never()).save(any(OrdenCompra.class));
    }

    @Test
    void deberiaRechazarEstadoInvalido() {
        OrdenCompraRequestDTO dto = dtoValido();
        dto.setEstado("BORRADOR");

        when(clienteClient.obtenerCliente(1L, null)).thenReturn(clienteActivo());
        when(inventarioClient.obtenerInventario(1L, null)).thenReturn(inventarioDisponible());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));

        assertTrue(ex.getMessage().contains("PENDIENTE"));
        verify(repository, never()).save(any(OrdenCompra.class));
    }

    @Test
    void deberiaCalcularTotalConPrecioNumericoNoEntero() {
        OrdenCompraRequestDTO dto = dtoValido();
        InventarioDTO inventario = new InventarioDTO(1L, Map.of("precio", 15000L), 100, 10, "Bodega A", "DISPONIBLE");

        when(clienteClient.obtenerCliente(1L, null)).thenReturn(clienteActivo());
        when(inventarioClient.obtenerInventario(1L, null)).thenReturn(inventario);
        when(repository.save(any(OrdenCompra.class))).thenAnswer(inv -> {
            OrdenCompra o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        OrdenCompraResponseDTO resultado = service.guardar(dto, null);

        assertEquals(30000, resultado.getTotal());
    }

    @Test
    void deberiaActualizarOrdenCorrectamente() {
        OrdenCompra existente = new OrdenCompra(1L, 1L, 1L, 2, 30000, "PENDIENTE", LocalDate.now());
        OrdenCompraRequestDTO dto = dtoValido();
        dto.setEstado("APROBADA");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteClient.obtenerCliente(1L, null)).thenReturn(clienteActivo());
        when(inventarioClient.obtenerInventario(1L, null)).thenReturn(inventarioDisponible());
        when(repository.save(any(OrdenCompra.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdenCompraResponseDTO resultado = service.actualizar(1L, dto, null);

        assertEquals("APROBADA", resultado.getEstado());
        verify(repository).findById(1L);
        verify(repository).save(existente);
    }

    @Test
    void deberiaEliminarOrdenPendiente() {
        OrdenCompra orden = new OrdenCompra(1L, 1L, 1L, 2, 30000, "PENDIENTE", LocalDate.now());
        when(repository.findById(1L)).thenReturn(Optional.of(orden));

        service.eliminar(1L);

        verify(repository).findById(1L);
        verify(repository).delete(orden);
    }

    @Test
    void deberiaLanzarExcepcionAlEliminarOrdenAprobada() {
        OrdenCompra orden = new OrdenCompra(1L, 1L, 1L, 2, 30000, "APROBADA", LocalDate.now());
        when(repository.findById(1L)).thenReturn(Optional.of(orden));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.eliminar(1L));
        assertTrue(ex.getMessage().contains("orden aprobada"));
        verify(repository, never()).delete(any(OrdenCompra.class));
    }
}
