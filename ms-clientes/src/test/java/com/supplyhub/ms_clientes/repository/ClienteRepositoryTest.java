package com.supplyhub.ms_clientes.repository;

import com.supplyhub.ms_clientes.model.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository repository;

    @Test
    void debeGuardarClienteYBuscarPorRutEmpresa() {
        Cliente cliente = new Cliente(null, "76.123.456-7", "Empresa Test", "contacto@empresa.cl",
                "+56912345678", "Av. Principal 100", "ACTIVO");

        repository.save(cliente);

        Optional<Cliente> resultado = repository.findByRutEmpresa("76.123.456-7");

        assertTrue(resultado.isPresent());
        assertEquals("Empresa Test", resultado.get().getRazonSocial());
    }

    @Test
    void debeBuscarClientePorEmailIgnorandoMayusculas() {
        repository.save(new Cliente(null, "77.123.456-7", "Cliente Mail", "ventas@cliente.cl",
                "+56911111111", "Calle 1", "ACTIVO"));

        Optional<Cliente> resultado = repository.findByEmailIgnoreCase("VENTAS@CLIENTE.CL");

        assertTrue(resultado.isPresent());
        assertEquals("Cliente Mail", resultado.get().getRazonSocial());
    }

    @Test
    void debeEliminarClienteGuardado() {
        Cliente guardado = repository.save(new Cliente(null, "78.123.456-7", "Temporal", "tmp@cliente.cl",
                "+56922222222", "Calle 2", "ACTIVO"));

        repository.deleteById(guardado.getId());

        assertTrue(repository.findById(guardado.getId()).isEmpty());
    }
}
