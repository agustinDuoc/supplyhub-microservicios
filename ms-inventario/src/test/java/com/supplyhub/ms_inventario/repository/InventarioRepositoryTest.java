package com.supplyhub.ms_inventario.repository;

import com.supplyhub.ms_inventario.model.Inventario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class InventarioRepositoryTest {

    @Autowired
    private InventarioRepository repository;

    @Test
    void debeGuardarInventarioYBuscarPorIdProducto() {
        Inventario inventario = new Inventario(null, 10L, 50, 5, "Bodega A", "ACTIVO");

        repository.save(inventario);

        Optional<Inventario> resultado = repository.findByIdProducto(10L);

        assertTrue(resultado.isPresent());
        assertEquals(50, resultado.get().getStockDisponible());
    }

    @Test
    void debeActualizarStockDeInventario() {
        Inventario guardado = repository.save(new Inventario(null, 20L, 10, 3, "Bodega B", "ACTIVO"));

        guardado.setStockDisponible(25);
        Inventario actualizado = repository.save(guardado);

        assertEquals(25, repository.findById(actualizado.getId()).orElseThrow().getStockDisponible());
    }

    @Test
    void debeEliminarInventarioGuardado() {
        Inventario guardado = repository.save(new Inventario(null, 30L, 15, 2, "Bodega C", "ACTIVO"));

        repository.deleteById(guardado.getId());

        assertTrue(repository.findById(guardado.getId()).isEmpty());
    }
}
