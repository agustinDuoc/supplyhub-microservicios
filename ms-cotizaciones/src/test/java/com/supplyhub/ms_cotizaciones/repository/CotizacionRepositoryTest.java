package com.supplyhub.ms_cotizaciones.repository;

import com.supplyhub.ms_cotizaciones.model.Cotizacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CotizacionRepositoryTest {

    @Autowired
    private CotizacionRepository repository;

    @Test
    void debeGuardarCotizacionYBuscarPorId() {
        Cotizacion cotizacion = new Cotizacion(null, 1L, 5, 25000, "CREADA");

        Cotizacion guardada = repository.save(cotizacion);

        assertNotNull(guardada.getId());
        assertEquals("CREADA", repository.findById(guardada.getId()).orElseThrow().getEstado());
    }

    @Test
    void debeListarCotizacionesGuardadas() {
        repository.save(new Cotizacion(null, 1L, 2, 10000, "CREADA"));
        repository.save(new Cotizacion(null, 2L, 3, 15000, "APROBADA"));

        assertTrue(repository.findAll().size() >= 2);
    }

    @Test
    void debeEliminarCotizacionGuardada() {
        Cotizacion guardada = repository.save(new Cotizacion(null, 3L, 1, 5000, "CREADA"));

        repository.deleteById(guardada.getId());

        assertTrue(repository.findById(guardada.getId()).isEmpty());
    }
}
