package com.supplyhub.ms_ordenes_compra.repository;

import com.supplyhub.ms_ordenes_compra.model.OrdenCompra;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OrdenCompraRepositoryTest {

    @Autowired
    private OrdenCompraRepository repository;

    @Test
    void debeGuardarOrdenCompraYBuscarPorId() {
        OrdenCompra orden = new OrdenCompra(null, 1L, 2L, 4, 120000, "CREADA", LocalDate.now());

        OrdenCompra guardada = repository.save(orden);

        assertNotNull(guardada.getId());
        assertEquals("CREADA", repository.findById(guardada.getId()).orElseThrow().getEstado());
    }

    @Test
    void debeListarOrdenesGuardadas() {
        repository.save(new OrdenCompra(null, 1L, 2L, 1, 30000, "CREADA", LocalDate.now()));
        repository.save(new OrdenCompra(null, 2L, 3L, 2, 60000, "PAGADA", LocalDate.now()));

        assertTrue(repository.findAll().size() >= 2);
    }

    @Test
    void debeEliminarOrdenCompraGuardada() {
        OrdenCompra guardada = repository.save(new OrdenCompra(null, 1L, 2L, 1, 30000, "CREADA", LocalDate.now()));

        repository.deleteById(guardada.getId());

        assertTrue(repository.findById(guardada.getId()).isEmpty());
    }
}
