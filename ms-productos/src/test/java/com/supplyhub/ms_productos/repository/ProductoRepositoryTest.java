package com.supplyhub.ms_productos.repository;

import com.supplyhub.ms_productos.model.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository repository;

    @Test
    void debeGuardarProductoYBuscarPorNombreIgnorandoMayusculas() {
        Producto producto = new Producto(null, "Taladro", "Taladro percutor", 49990, 1L, 1L, "ACTIVO");

        repository.save(producto);

        Optional<Producto> resultado = repository.findByNombreIgnoreCase("TALADRO");

        assertTrue(resultado.isPresent());
        assertEquals(49990, resultado.get().getPrecio());
    }

    @Test
    void debeRetornarVacioCuandoNombreNoExiste() {
        Optional<Producto> resultado = repository.findByNombreIgnoreCase("Producto inexistente");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeEliminarProductoGuardado() {
        Producto guardado = repository.save(new Producto(null, "Temporal", "Producto temporal", 1000, 1L, 1L, "ACTIVO"));

        repository.deleteById(guardado.getId());

        assertTrue(repository.findById(guardado.getId()).isEmpty());
    }
}
