package com.supplyhub.ms_auth.repository;

import com.supplyhub.ms_auth.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    void debeGuardarUsuarioYBuscarPorUsername() {
        Usuario usuario = Usuario.builder()
                .username("agustin")
                .password("secreta")
                .role("CLIENTE")
                .build();

        repository.save(usuario);

        Optional<Usuario> resultado = repository.findByUsername("agustin");

        assertTrue(resultado.isPresent());
        assertEquals("CLIENTE", resultado.get().getRole());
    }

    @Test
    void debeRetornarVacioCuandoUsernameNoExiste() {
        Optional<Usuario> resultado = repository.findByUsername("no_existe");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeEliminarUsuarioGuardado() {
        Usuario guardado = repository.save(new Usuario(null, "temporal", "password", "ADMIN"));

        repository.deleteById(guardado.getId());

        assertTrue(repository.findById(guardado.getId()).isEmpty());
    }
}
