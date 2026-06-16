package com.supplyhub.ms_pagos.service;

import com.supplyhub.ms_pagos.client.OrdenCompraClient;
import com.supplyhub.ms_pagos.dto.OrdenCompraDTO;
import com.supplyhub.ms_pagos.dto.PagoRequestDTO;
import com.supplyhub.ms_pagos.dto.PagoResponseDTO;
import com.supplyhub.ms_pagos.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_pagos.model.Pago;
import com.supplyhub.ms_pagos.repository.PagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository repository;

    @Mock
    private OrdenCompraClient ordenCompraClient;

    @InjectMocks
    private PagoService service;

    private OrdenCompraDTO ordenPagable() {
        return new OrdenCompraDTO(1L, null, null, 2, 30000, "PENDIENTE", LocalDate.now());
    }

    private PagoRequestDTO dtoValido() {
        PagoRequestDTO dto = new PagoRequestDTO();
        dto.setIdOrdenCompra(1L);
        dto.setMonto(30000);
        dto.setMetodoPago("TRANSFERENCIA");
        dto.setEstadoPago("APROBADO");
        return dto;
    }

    @Test
    void deberiaRetornarPagoCuandoExiste() {
        Pago pago = new Pago(1L, 1L, 30000, "TRANSFERENCIA", "APROBADO", LocalDate.now());
        when(repository.findById(1L)).thenReturn(Optional.of(pago));
        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(ordenPagable());

        PagoResponseDTO resultado = service.buscarPorId(1L, null);

        assertNotNull(resultado);
        assertEquals(30000, resultado.getMonto());
        verify(repository).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoPagoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.buscarPorId(99L, null));
        verify(repository).findById(99L);
    }

    @Test
    void deberiaRetornarListaPagos() {
        Pago pago = new Pago(1L, 1L, 30000, "TRANSFERENCIA", "APROBADO", LocalDate.now());
        when(repository.findAll()).thenReturn(List.of(pago));
        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(ordenPagable());

        List<PagoResponseDTO> resultado = service.listar(null);

        assertEquals(1, resultado.size());
        assertEquals("TRANSFERENCIA", resultado.get(0).getMetodoPago());
        verify(repository).findAll();
    }

    @Test
    void deberiaCrearPagoCorrectamente() {
        PagoRequestDTO dto = dtoValido();
        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(ordenPagable());
        when(repository.findByIdOrdenCompra(1L)).thenReturn(Optional.empty());
        when(repository.save(any(Pago.class))).thenAnswer(inv -> {
            Pago p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        PagoResponseDTO resultado = service.guardar(dto, null);

        assertEquals(1L, resultado.getId());
        assertEquals(30000, resultado.getMonto());
        verify(repository).save(any(Pago.class));
    }

    @Test
    void deberiaActualizarPagoCorrectamente() {
        Pago existente = new Pago(1L, 1L, 30000, "TRANSFERENCIA", "PENDIENTE", LocalDate.now());
        PagoRequestDTO dto = dtoValido();
        dto.setMetodoPago("TARJETA");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(ordenPagable());
        when(repository.findByIdOrdenCompra(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        PagoResponseDTO resultado = service.actualizar(1L, dto, null);

        assertEquals("TARJETA", resultado.getMetodoPago());
        verify(repository).findById(1L);
        verify(repository).save(existente);
    }

    @Test
    void deberiaEliminarPagoPendiente() {
        Pago pago = new Pago(1L, 1L, 30000, "TRANSFERENCIA", "PENDIENTE", LocalDate.now());
        when(repository.findById(1L)).thenReturn(Optional.of(pago));

        service.eliminar(1L);

        verify(repository).findById(1L);
        verify(repository).delete(pago);
    }

    @Test
    void deberiaLanzarExcepcionAlEliminarPagoAprobado() {
        Pago pago = new Pago(1L, 1L, 30000, "TRANSFERENCIA", "APROBADO", LocalDate.now());
        when(repository.findById(1L)).thenReturn(Optional.of(pago));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.eliminar(1L));
        assertTrue(ex.getMessage().contains("pago aprobado"));
        verify(repository, never()).delete(any(Pago.class));
    }

    @Test
    void deberiaRechazarMontoInsuficiente() {
        PagoRequestDTO dto = dtoValido();
        dto.setMonto(10000);
        when(ordenCompraClient.obtenerOrdenCompra(1L, null)).thenReturn(ordenPagable());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));
        assertTrue(ex.getMessage().contains("no cubre el total"));
        verify(repository, never()).save(any(Pago.class));
    }
}
