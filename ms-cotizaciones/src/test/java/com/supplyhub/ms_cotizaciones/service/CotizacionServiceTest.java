package com.supplyhub.ms_cotizaciones.service;

import com.supplyhub.ms_cotizaciones.client.ProductoClient;
import com.supplyhub.ms_cotizaciones.dto.CotizacionRequestDTO;
import com.supplyhub.ms_cotizaciones.dto.CotizacionResponseDTO;
import com.supplyhub.ms_cotizaciones.dto.ProductoDTO;
import com.supplyhub.ms_cotizaciones.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_cotizaciones.model.Cotizacion;
import com.supplyhub.ms_cotizaciones.repository.CotizacionRepository;
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
class CotizacionServiceTest {

    @Mock
    private CotizacionRepository repository;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private CotizacionService service;

    private ProductoDTO productoActivo() {
        return new ProductoDTO(1L, "Martillo", "Martillo de acero", 15000, null, null, "ACTIVO");
    }

    private CotizacionRequestDTO dtoValido() {
        CotizacionRequestDTO dto = new CotizacionRequestDTO();
        dto.setIdProducto(1L);
        dto.setCantidad(2);
        dto.setEstado("VIGENTE");
        return dto;
    }

    @Test
    void deberiaRetornarCotizacionCuandoExiste() {
        Cotizacion cotizacion = new Cotizacion(1L, 1L, 2, 30000, "VIGENTE");
        when(repository.findById(1L)).thenReturn(Optional.of(cotizacion));
        when(productoClient.obtenerProducto(1L, null)).thenReturn(productoActivo());

        CotizacionResponseDTO resultado = service.buscarPorId(1L, null);

        assertNotNull(resultado);
        assertEquals(30000, resultado.getTotal());
        verify(repository).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoCotizacionNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.buscarPorId(99L, null));
        verify(repository).findById(99L);
    }

    @Test
    void deberiaRetornarListaCotizaciones() {
        Cotizacion cotizacion = new Cotizacion(1L, 1L, 2, 30000, "VIGENTE");
        when(repository.findAll()).thenReturn(List.of(cotizacion));
        when(productoClient.obtenerProducto(1L, null)).thenReturn(productoActivo());

        List<CotizacionResponseDTO> resultado = service.listar(null);

        assertEquals(1, resultado.size());
        assertEquals("VIGENTE", resultado.get(0).getEstado());
        verify(repository).findAll();
    }

    @Test
    void deberiaCrearCotizacionCorrectamente() {
        CotizacionRequestDTO dto = dtoValido();
        when(productoClient.obtenerProducto(1L, null)).thenReturn(productoActivo());
        when(repository.save(any(Cotizacion.class))).thenAnswer(inv -> {
            Cotizacion c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        CotizacionResponseDTO resultado = service.guardar(dto, null);

        assertEquals(1L, resultado.getId());
        assertEquals(30000, resultado.getTotal());
        verify(repository).save(any(Cotizacion.class));
    }

    @Test
    void deberiaActualizarCotizacionCorrectamente() {
        Cotizacion existente = new Cotizacion(1L, 1L, 2, 30000, "VIGENTE");
        CotizacionRequestDTO dto = dtoValido();
        dto.setEstado("ACEPTADA");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoClient.obtenerProducto(1L, null)).thenReturn(productoActivo());
        when(repository.save(any(Cotizacion.class))).thenAnswer(inv -> inv.getArgument(0));

        CotizacionResponseDTO resultado = service.actualizar(1L, dto, null);

        assertEquals("ACEPTADA", resultado.getEstado());
        verify(repository).findById(1L);
        verify(repository).save(existente);
    }

    @Test
    void deberiaEliminarCotizacionPorId() {
        Cotizacion cotizacion = new Cotizacion(1L, 1L, 2, 30000, "VIGENTE");
        when(repository.findById(1L)).thenReturn(Optional.of(cotizacion));

        service.eliminar(1L);

        verify(repository).findById(1L);
        verify(repository).delete(cotizacion);
    }

    @Test
    void deberiaLanzarExcepcionConProductoInactivo() {
        CotizacionRequestDTO dto = dtoValido();
        ProductoDTO inactivo = productoActivo();
        inactivo.setEstado("INACTIVO");
        when(productoClient.obtenerProducto(1L, null)).thenReturn(inactivo);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto, null));
        assertTrue(ex.getMessage().contains("producto inactivo"));
        verify(repository, never()).save(any(Cotizacion.class));
    }
}
