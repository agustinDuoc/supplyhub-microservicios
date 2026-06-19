package com.supplyhub.ms_despachos.repository;

import com.supplyhub.ms_despachos.model.Despacho;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class DespachoRepositoryTest {

    @Autowired
    private DespachoRepository repository;

    @Test
    void debeGuardarDespachoYBuscarPorIdOrdenCompra() {
        Despacho despacho = new Despacho(null, 10L, 20L, "Av. Entrega 123", "EN_PREPARACION",
                LocalDate.now(), null);

        repository.save(despacho);

        Optional<Despacho> resultado = repository.findByIdOrdenCompra(10L);

        assertTrue(resultado.isPresent());
        assertEquals("EN_PREPARACION", resultado.get().getEstadoDespacho());
    }

    @Test
    void debeBuscarDespachoPorIdPago() {
        repository.save(new Despacho(null, 11L, 21L, "Av. Pago 456", "DESPACHADO",
                LocalDate.now(), LocalDate.now().plusDays(2)));

        Optional<Despacho> resultado = repository.findByIdPago(21L);

        assertTrue(resultado.isPresent());
        assertEquals("DESPACHADO", resultado.get().getEstadoDespacho());
    }

    @Test
    void debeEliminarDespachoGuardado() {
        Despacho guardado = repository.save(new Despacho(null, 12L, 22L, "Av. Temporal", "EN_PREPARACION",
                LocalDate.now(), null));

        repository.deleteById(guardado.getId());

        assertTrue(repository.findById(guardado.getId()).isEmpty());
    }
}
