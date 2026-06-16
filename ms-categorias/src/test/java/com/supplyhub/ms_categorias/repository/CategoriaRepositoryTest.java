package com.supplyhub.ms_categorias.repository;

import com.supplyhub.ms_categorias.model.Categoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository repository;

    @Test
    void debeGuardarCategoria() {
        Categoria categoria = new Categoria(null, "Seguridad", "Productos de seguridad", "ACTIVO");

        Categoria guardada = repository.save(categoria);

        assertNotNull(guardada.getId());
        assertEquals("Seguridad", guardada.getNombre());
    }

    @Test
    void debeBuscarCategoriaPorId() {
        Categoria guardada = repository.save(new Categoria(null, "Herramientas", "Herramientas manuales", "ACTIVO"));

        Optional<Categoria> resultado = repository.findById(guardada.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Herramientas", resultado.get().getNombre());
    }

    @Test
    void debeListarCategorias() {
        repository.save(new Categoria(null, "Repuestos", "Piezas", "ACTIVO"));
        repository.save(new Categoria(null, "Electricidad", "Material eléctrico", "ACTIVO"));

        List<Categoria> resultado = repository.findAll();

        assertTrue(resultado.size() >= 2);
    }

    @Test
    void debeEliminarCategoria() {
        Categoria guardada = repository.save(new Categoria(null, "Temporal", "Test", "ACTIVO"));

        repository.deleteById(guardada.getId());

        assertFalse(repository.findById(guardada.getId()).isPresent());
    }
}
