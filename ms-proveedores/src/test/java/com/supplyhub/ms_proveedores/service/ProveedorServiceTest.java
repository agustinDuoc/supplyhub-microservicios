package com.supplyhub.ms_proveedores.service;

import com.supplyhub.ms_proveedores.dto.ProveedorRequestDTO;
import com.supplyhub.ms_proveedores.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_proveedores.model.Proveedor;
import com.supplyhub.ms_proveedores.repository.ProveedorRepository;
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
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository repository;

    @InjectMocks
    private ProveedorService service;

    private ProveedorRequestDTO dtoValido() {
        ProveedorRequestDTO dto = new ProveedorRequestDTO();
        dto.setRutProveedor("76.987.654-3");
        dto.setNombre("Proveedor Test");
        dto.setEmail("proveedor@test.com");
        dto.setTelefono("+56987654321");
        dto.setDireccion("Av. Industrial 50");
        dto.setEstado("ACTIVO");
        return dto;
    }

    @Test
    void deberiaRetornarProveedorCuandoExiste() {
        Proveedor proveedor = new Proveedor(1L, "76.987.654-3", "Proveedor Test", "proveedor@test.com",
                "+56987654321", "Av. Industrial 50", "ACTIVO");
        when(repository.findById(1L)).thenReturn(Optional.of(proveedor));

        Proveedor resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Proveedor Test", resultado.getNombre());
        verify(repository).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoProveedorNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> service.buscarPorId(99L)
        );

        assertTrue(ex.getMessage().contains("99"));
        verify(repository).findById(99L);
    }

    @Test
    void deberiaRetornarListaProveedores() {
        Proveedor proveedor = new Proveedor(1L, "76.987.654-3", "Proveedor Test", "proveedor@test.com",
                "+56987654321", "Av. Industrial 50", "ACTIVO");
        when(repository.findAll()).thenReturn(List.of(proveedor));

        List<Proveedor> resultado = service.listar();

        assertEquals(1, resultado.size());
        assertEquals("Proveedor Test", resultado.get(0).getNombre());
        verify(repository).findAll();
    }

    @Test
    void deberiaCrearProveedorCorrectamente() {
        ProveedorRequestDTO dto = dtoValido();
        when(repository.findByRutProveedor(dto.getRutProveedor())).thenReturn(Optional.empty());
        when(repository.save(any(Proveedor.class))).thenAnswer(inv -> {
            Proveedor p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        Proveedor resultado = service.guardar(dto);

        assertEquals(1L, resultado.getId());
        assertEquals("proveedor@test.com", resultado.getEmail());
        verify(repository).save(any(Proveedor.class));
    }

    @Test
    void deberiaActualizarProveedorCorrectamente() {
        Proveedor existente = new Proveedor(1L, "76.987.654-3", "Nombre viejo", "viejo@test.com",
                "+56987654321", "Direccion vieja", "ACTIVO");
        ProveedorRequestDTO dto = dtoValido();
        dto.setNombre("Nombre nuevo");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.findByRutProveedor(dto.getRutProveedor())).thenReturn(Optional.of(existente));
        when(repository.save(any(Proveedor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Proveedor resultado = service.actualizar(1L, dto);

        assertEquals("Nombre nuevo", resultado.getNombre());
        verify(repository).findById(1L);
        verify(repository).save(existente);
    }

    @Test
    void deberiaEliminarProveedorPorId() {
        Proveedor proveedor = new Proveedor(1L, "76.987.654-3", "Eliminar", "eliminar@test.com",
                "+56987654321", "Direccion", "ACTIVO");
        when(repository.findById(1L)).thenReturn(Optional.of(proveedor));

        service.eliminar(1L);

        verify(repository).findById(1L);
        verify(repository).delete(proveedor);
    }

    @Test
    void deberiaLanzarExcepcionAlActualizarProveedorInexistente() {
        ProveedorRequestDTO dto = dtoValido();
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.actualizar(99L, dto));
        verify(repository).findById(99L);
        verify(repository, never()).save(any(Proveedor.class));
    }
}
