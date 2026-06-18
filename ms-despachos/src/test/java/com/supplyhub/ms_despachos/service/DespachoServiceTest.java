package com.supplyhub.ms_despachos.service;

import com.supplyhub.ms_despachos.client.OrdenCompraClient;
import com.supplyhub.ms_despachos.client.PagoClient;
import com.supplyhub.ms_despachos.dto.DespachoRequestDTO;
import com.supplyhub.ms_despachos.dto.DespachoResponseDTO;
import com.supplyhub.ms_despachos.dto.OrdenCompraDTO;
import com.supplyhub.ms_despachos.dto.PagoDTO;
import com.supplyhub.ms_despachos.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_despachos.model.Despacho;
import com.supplyhub.ms_despachos.repository.DespachoRepository;
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
class DespachoServiceTest {

    @Mock
    private DespachoRepository repository;

    @Mock
    private OrdenCompraClient ordenCompraClient;

    @Mock
    private PagoClient pagoClient;

    @InjectMocks
    private DespachoService service;

    private OrdenCompraDTO ordenDespachable() {
        return new OrdenCompraDTO(1L, null, null, 2, 30000, "APROBADA", LocalDate.now());
    }

    private PagoDTO pagoAprobado() {
        return new PagoDTO(1L, Map.of("id", 1L), 30000, "TRANSFERENCIA", "APROBADO", LocalDate.now());
    }

    private DespachoRequestDTO dtoValido() {
        DespachoRequestDTO dto = new DespachoRequestDTO();
        dto.setIdOrdenCompra(1L);
        dto.setIdPago(1L);
        dto.setDireccionEnvio("Av. Entrega 123");
        dto.setEstadoDespacho("EN_PREPARACION");
        dto.setFechaEntrega(LocalDate.now().plusDays(3));
        return dto;
    }

    @Test
    void deberiaRetornarDespachoCuandoExiste() {
        Despacho despacho = new Despacho(1L, 1L, 1L, "Av. Entrega 123", "EN_PREPARACION",
                LocalDate.now(), LocalDate.now().plusDays(3));
        when(repository.findById(1L)).thenReturn(Optional.of(despacho));
        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(ordenDespachable());
        when(pagoClient.obtenerPago(1L, null)).thenReturn(pagoAprobado());

        DespachoResponseDTO resultado = service.buscarPorId(1L, null);

        assertNotNull(resultado);
        assertEquals("Av. Entrega 123", resultado.getDireccionEnvio());
        verify(repository).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoDespachoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.buscarPorId(99L, null));
        verify(repository).findById(99L);
    }

    @Test
    void deberiaRetornarListaDespachos() {
        Despacho despacho = new Despacho(1L, 1L, 1L, "Av. Entrega 123", "EN_PREPARACION",
                LocalDate.now(), LocalDate.now().plusDays(3));
        when(repository.findAll()).thenReturn(List.of(despacho));
        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(ordenDespachable());
        when(pagoClient.obtenerPago(1L, null)).thenReturn(pagoAprobado());

        List<DespachoResponseDTO> resultado = service.listar(null);

        assertEquals(1, resultado.size());
        assertEquals("EN_PREPARACION", resultado.get(0).getEstadoDespacho());
        verify(repository).findAll();
    }

    @Test
    void deberiaCrearDespachoCorrectamente() {
        DespachoRequestDTO dto = dtoValido();
        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(ordenDespachable());
        when(pagoClient.obtenerPago(1L, null)).thenReturn(pagoAprobado());
        when(repository.findByIdOrdenCompra(1L)).thenReturn(Optional.empty());
        when(repository.findByIdPago(1L)).thenReturn(Optional.empty());
        when(repository.save(any(Despacho.class))).thenAnswer(inv -> {
            Despacho d = inv.getArgument(0);
            d.setId(1L);
            return d;
        });

        DespachoResponseDTO resultado = service.guardar(dto, null);

        assertEquals(1L, resultado.getId());
        assertEquals("Av. Entrega 123", resultado.getDireccionEnvio());
        verify(repository).save(any(Despacho.class));
    }

    @Test
    void deberiaRechazarOrdenCancelada() {
        DespachoRequestDTO dto = dtoValido();
        OrdenCompraDTO cancelada = ordenDespachable();
        cancelada.setEstado("CANCELADA");
        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(cancelada);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));

        assertTrue(ex.getMessage().contains("orden cancelada"));
        verify(repository, never()).save(any(Despacho.class));
    }

    @Test
    void deberiaRechazarPagoNoAprobado() {
        DespachoRequestDTO dto = dtoValido();
        PagoDTO pendiente = pagoAprobado();
        pendiente.setEstadoPago("PENDIENTE");

        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(ordenDespachable());
        when(pagoClient.obtenerPago(1L, null)).thenReturn(pendiente);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));

        assertTrue(ex.getMessage().contains("pago no está aprobado"));
        verify(repository, never()).save(any(Despacho.class));
    }

    @Test
    void deberiaRechazarPagoDeOtraOrden() {
        DespachoRequestDTO dto = dtoValido();
        PagoDTO pagoOtraOrden = new PagoDTO(1L, Map.of("id", 99L), 30000, "TRANSFERENCIA", "APROBADO",
                LocalDate.now());

        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(ordenDespachable());
        when(pagoClient.obtenerPago(1L, null)).thenReturn(pagoOtraOrden);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));

        assertTrue(ex.getMessage().contains("no corresponde"));
        verify(repository, never()).save(any(Despacho.class));
    }

    @Test
    void deberiaRechazarFechaEntregaPasada() {
        DespachoRequestDTO dto = dtoValido();
        dto.setFechaEntrega(LocalDate.now().minusDays(1));

        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(ordenDespachable());
        when(pagoClient.obtenerPago(1L, null)).thenReturn(pagoAprobado());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));

        assertTrue(ex.getMessage().contains("fecha de entrega"));
        verify(repository, never()).save(any(Despacho.class));
    }

    @Test
    void deberiaRechazarDespachoDuplicadoPorOrden() {
        DespachoRequestDTO dto = dtoValido();
        Despacho existente = new Despacho(2L, 1L, 2L, "Av. Antigua", "ENVIADO",
                LocalDate.now(), LocalDate.now().plusDays(2));

        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(ordenDespachable());
        when(pagoClient.obtenerPago(1L, null)).thenReturn(pagoAprobado());
        when(repository.findByIdOrdenCompra(1L)).thenReturn(Optional.of(existente));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));

        assertTrue(ex.getMessage().contains("Ya existe"));
        verify(repository, never()).save(any(Despacho.class));
    }

    @Test
    void deberiaRechazarDespachoDuplicadoPorPago() {
        DespachoRequestDTO dto = dtoValido();
        Despacho existente = new Despacho(2L, 2L, 1L, "Av. Antigua", "ENVIADO",
                LocalDate.now(), LocalDate.now().plusDays(2));

        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(ordenDespachable());
        when(pagoClient.obtenerPago(1L, null)).thenReturn(pagoAprobado());
        when(repository.findByIdOrdenCompra(1L)).thenReturn(Optional.empty());
        when(repository.findByIdPago(1L)).thenReturn(Optional.of(existente));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));

        assertTrue(ex.getMessage().contains("Ya existe"));
        verify(repository, never()).save(any(Despacho.class));
    }

    @Test
    void deberiaRechazarEstadoDespachoInvalido() {
        DespachoRequestDTO dto = dtoValido();
        dto.setEstadoDespacho("BORRADOR");

        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(ordenDespachable());
        when(pagoClient.obtenerPago(1L, null)).thenReturn(pagoAprobado());
        when(repository.findByIdOrdenCompra(1L)).thenReturn(Optional.empty());
        when(repository.findByIdPago(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));

        assertTrue(ex.getMessage().contains("estado del despacho"));
        verify(repository, never()).save(any(Despacho.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoOrdenExternaNoExiste() {
        DespachoRequestDTO dto = dtoValido();
        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(null);

        assertThrows(RecursoNoEncontradoException.class, () -> service.guardar(dto, null));
        verify(repository, never()).save(any(Despacho.class));
    }

    @Test
    void deberiaActualizarDespachoCorrectamente() {
        Despacho existente = new Despacho(1L, 1L, 1L, "Av. Vieja", "EN_PREPARACION",
                LocalDate.now(), LocalDate.now().plusDays(3));
        DespachoRequestDTO dto = dtoValido();
        dto.setDireccionEnvio("Av. Nueva 456");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(ordenDespachable());
        when(pagoClient.obtenerPago(1L, null)).thenReturn(pagoAprobado());
        when(repository.findByIdOrdenCompra(1L)).thenReturn(Optional.of(existente));
        when(repository.findByIdPago(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Despacho.class))).thenAnswer(inv -> inv.getArgument(0));

        DespachoResponseDTO resultado = service.actualizar(1L, dto, null);

        assertEquals("Av. Nueva 456", resultado.getDireccionEnvio());
        verify(repository).findById(1L);
        verify(repository).save(existente);
    }

    @Test
    void deberiaEliminarDespachoNoEntregado() {
        Despacho despacho = new Despacho(1L, 1L, 1L, "Av. Entrega", "ENVIADO",
                LocalDate.now(), LocalDate.now().plusDays(3));
        when(repository.findById(1L)).thenReturn(Optional.of(despacho));

        service.eliminar(1L);

        verify(repository).findById(1L);
        verify(repository).delete(despacho);
    }

    @Test
    void deberiaLanzarExcepcionAlEliminarDespachoEntregado() {
        Despacho despacho = new Despacho(1L, 1L, 1L, "Av. Entrega", "ENTREGADO",
                LocalDate.now(), LocalDate.now());
        when(repository.findById(1L)).thenReturn(Optional.of(despacho));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.eliminar(1L));
        assertTrue(ex.getMessage().contains("despacho entregado"));
        verify(repository, never()).delete(any(Despacho.class));
    }
}
