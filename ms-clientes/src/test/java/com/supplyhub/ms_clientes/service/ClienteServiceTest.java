package com.supplyhub.ms_clientes.service;

import com.supplyhub.ms_clientes.dto.ClienteRequestDTO;
import com.supplyhub.ms_clientes.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_clientes.model.Cliente;
import com.supplyhub.ms_clientes.repository.ClienteRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    private ClienteRequestDTO dtoValido() {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setRutEmpresa("76.123.456-7");
        dto.setRazonSocial("Empresa Test");
        dto.setEmail("test@empresa.com");
        dto.setTelefono("+56912345678");
        dto.setDireccion("Av. Principal 100");
        dto.setEstado("ACTIVO");
        return dto;
    }

    @Test
    void deberiaRetornarClienteCuandoExiste() {
        Cliente cliente = new Cliente(1L, "76.123.456-7", "Empresa Test", "test@empresa.com",
                "+56912345678", "Av. Principal 100", "ACTIVO");
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Empresa Test", resultado.getRazonSocial());
        verify(repository).findById(1L);
    }

    @Test
    void deberiaLanzarExcepcionCuandoClienteNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> service.buscarPorId(99L)
        );

        assertTrue(ex.getMessage().contains("99"));
        verify(repository).findById(99L);
    }

    @Test
    void deberiaRetornarListaClientes() {
        Cliente cliente = new Cliente(1L, "76.123.456-7", "Empresa Test", "test@empresa.com",
                "+56912345678", "Av. Principal 100", "ACTIVO");
        when(repository.findAll()).thenReturn(List.of(cliente));

        List<Cliente> resultado = service.listar();

        assertEquals(1, resultado.size());
        assertEquals("Empresa Test", resultado.get(0).getRazonSocial());
        verify(repository).findAll();
    }

    @Test
    void deberiaCrearClienteCorrectamente() {
        ClienteRequestDTO dto = dtoValido();
        when(repository.findByRutEmpresa(dto.getRutEmpresa())).thenReturn(Optional.empty());
        when(repository.findByEmailIgnoreCase(dto.getEmail())).thenReturn(Optional.empty());
        when(repository.save(any(Cliente.class))).thenAnswer(inv -> {
            Cliente c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        Cliente resultado = service.guardar(dto);

        assertEquals(1L, resultado.getId());
        assertEquals("test@empresa.com", resultado.getEmail());
        verify(repository).save(any(Cliente.class));
    }

    @Test
    void deberiaActualizarClienteCorrectamente() {
        Cliente existente = new Cliente(1L, "76.123.456-7", "Nombre viejo", "viejo@empresa.com",
                "+56912345678", "Direccion vieja", "ACTIVO");
        ClienteRequestDTO dto = dtoValido();
        dto.setRazonSocial("Nombre nuevo");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.findByRutEmpresa(dto.getRutEmpresa())).thenReturn(Optional.of(existente));
        when(repository.findByEmailIgnoreCase(dto.getEmail())).thenReturn(Optional.of(existente));
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente resultado = service.actualizar(1L, dto);

        assertEquals("Nombre nuevo", resultado.getRazonSocial());
        verify(repository).findById(1L);
        verify(repository).save(existente);
    }

    @Test
    void deberiaEliminarClientePorId() {
        Cliente cliente = new Cliente(1L, "76.123.456-7", "Eliminar", "eliminar@empresa.com",
                "+56912345678", "Direccion", "ACTIVO");
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));

        service.eliminar(1L);

        verify(repository).findById(1L);
        verify(repository).delete(cliente);
    }

    @Test
    void deberiaLanzarExcepcionAlActualizarClienteInexistente() {
        ClienteRequestDTO dto = dtoValido();
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.actualizar(99L, dto));
        verify(repository).findById(99L);
        verify(repository, never()).save(any(Cliente.class));
    }

}
