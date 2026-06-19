package com.supplyhub.ms_proveedores.repository;

import com.supplyhub.ms_proveedores.model.Proveedor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ProveedorRepositoryTest {

    @Autowired
    private ProveedorRepository repository;

    @Test
    void debeGuardarProveedorYBuscarPorRutProveedor() {
        Proveedor proveedor = new Proveedor(null, "96.123.456-7", "Proveedor Test", "proveedor@test.cl",
                "+56223456789", "Bodega Central", "ACTIVO");

        repository.save(proveedor);

        Optional<Proveedor> resultado = repository.findByRutProveedor("96.123.456-7");

        assertTrue(resultado.isPresent());
        assertEquals("Proveedor Test", resultado.get().getNombre());
    }

    @Test
    void debeRetornarVacioCuandoRutNoExiste() {
        Optional<Proveedor> resultado = repository.findByRutProveedor("00.000.000-0");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeListarProveedoresGuardados() {
        repository.save(new Proveedor(null, "96.111.111-1", "Proveedor A", "a@test.cl", "+562111", "Dir A", "ACTIVO"));
        repository.save(new Proveedor(null, "96.222.222-2", "Proveedor B", "b@test.cl", "+562222", "Dir B", "ACTIVO"));

        assertTrue(repository.findAll().size() >= 2);
    }
}
