package com.supplyhub.ms_categorias.service;

import com.supplyhub.ms_categorias.dto.CategoriaRequestDTO;
import com.supplyhub.ms_categorias.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_categorias.model.Categoria;
import com.supplyhub.ms_categorias.repository.CategoriaRepository;
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
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaService service;

    @Test
    void deberiaRetornarCategoriaCuandoExiste() {
        Categoria categoria = new Categoria(1L, "Seguridad", "Productos de seguridad", "ACTIVO");
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));

        Categoria resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Seguridad", resultado.getNombre());
        verify(repository).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoCategoriaNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> service.buscarPorId(99L)
        );

        assertTrue(ex.getMessage().contains("99"));
        verify(repository).findById(99L);
    }

    @Test
    void deberiaRetornarListaCategorias() {
        Categoria categoria = new Categoria(1L, "Herramientas", "Herramientas manuales", "ACTIVO");
        when(repository.findAll()).thenReturn(List.of(categoria));

        List<Categoria> resultado = service.listar();

        assertEquals(1, resultado.size());
        assertEquals("Herramientas", resultado.get(0).getNombre());
        verify(repository).findAll();
    }

    @Test
    void deberiaCrearCategoriaCorrectamente() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Repuestos");
        dto.setDescripcion("Piezas industriales");
        dto.setEstado("ACTIVO");

        Categoria guardada = new Categoria(1L, "Repuestos", "Piezas industriales", "ACTIVO");
        when(repository.save(any(Categoria.class))).thenReturn(guardada);

        Categoria resultado = service.guardar(dto);

        assertEquals(1L, resultado.getId());
        assertEquals("Repuestos", resultado.getNombre());
        verify(repository).save(any(Categoria.class));
    }

    @Test
    void deberiaRechazarCategoriaConNombreDuplicado() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Repuestos");
        dto.setDescripcion("Piezas industriales");
        dto.setEstado("ACTIVO");

        when(repository.findByNombreIgnoreCase("Repuestos"))
                .thenReturn(Optional.of(new Categoria(1L, "Repuestos", "Duplicada", "ACTIVO")));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto));

        assertTrue(ex.getMessage().contains("Ya existe"));
        verify(repository, never()).save(any(Categoria.class));
    }

    @Test
    void deberiaRechazarCategoriaConEstadoInvalido() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Repuestos");
        dto.setDescripcion("Piezas industriales");
        dto.setEstado("BORRADOR");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto));

        assertTrue(ex.getMessage().contains("ACTIVO o INACTIVO"));
        verify(repository, never()).save(any(Categoria.class));
    }

    @Test
    void deberiaRechazarCategoriaConNombreVacio() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("   ");
        dto.setDescripcion("Piezas industriales");
        dto.setEstado("ACTIVO");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.guardar(dto));

        assertTrue(ex.getMessage().contains("no puede estar vacío"));
        verify(repository, never()).save(any(Categoria.class));
    }

    @Test
    void deberiaActualizarCategoriaCorrectamente() {
        Categoria existente = new Categoria(1L, "Nombre viejo", "Descripcion vieja", "ACTIVO");
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Nombre nuevo");
        dto.setDescripcion("Descripcion nueva");
        dto.setEstado("ACTIVO");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Categoria resultado = service.actualizar(1L, dto);

        assertEquals("Nombre nuevo", resultado.getNombre());
        verify(repository).findById(1L);
        verify(repository).save(existente);
    }

    @Test
    void deberiaEliminarCategoriaPorId() {
        Categoria categoria = new Categoria(1L, "Eliminar", "Test", "ACTIVO");
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));

        service.eliminar(1L);

        verify(repository).findById(1L);
        verify(repository).delete(categoria);
    }

    @Test
    void deberiaLanzarExcepcionAlActualizarCategoriaInexistente() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Inexistente");
        dto.setDescripcion("Test");
        dto.setEstado("ACTIVO");

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.actualizar(99L, dto));
        verify(repository).findById(99L);
        verify(repository, never()).save(any(Categoria.class));
    }
}
