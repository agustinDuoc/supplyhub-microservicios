package com.supplyhub.ms_pagos.repository;

import com.supplyhub.ms_pagos.model.Pago;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PagoRepositoryTest {

    @Autowired
    private PagoRepository repository;

    @Test
    void debeGuardarPagoYBuscarPorIdOrdenCompra() {
        Pago pago = new Pago(null, 100L, 95000, "TRANSFERENCIA", "PAGADO", LocalDate.now());

        repository.save(pago);

        Optional<Pago> resultado = repository.findByIdOrdenCompra(100L);

        assertTrue(resultado.isPresent());
        assertEquals("PAGADO", resultado.get().getEstadoPago());
    }

    @Test
    void debeRetornarVacioCuandoOrdenNoTienePago() {
        Optional<Pago> resultado = repository.findByIdOrdenCompra(999L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeEliminarPagoGuardado() {
        Pago guardado = repository.save(new Pago(null, 101L, 50000, "DEBITO", "PAGADO", LocalDate.now()));

        repository.deleteById(guardado.getId());

        assertTrue(repository.findById(guardado.getId()).isEmpty());
    }
}
