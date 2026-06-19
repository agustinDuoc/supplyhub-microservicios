package com.supplyhub.ms_auth.repository;

import com.supplyhub.ms_auth.model.RefreshToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository repository;

    @Test
    void debeGuardarRefreshTokenYBuscarPorToken() {
        RefreshToken token = new RefreshToken(null, "refresh-123", "agustin",
                new Date(System.currentTimeMillis() + 86400000));

        repository.save(token);

        Optional<RefreshToken> resultado = repository.findByToken("refresh-123");

        assertTrue(resultado.isPresent());
        assertEquals("agustin", resultado.get().getUsername());
    }

    @Test
    void debeRetornarVacioCuandoTokenNoExiste() {
        Optional<RefreshToken> resultado = repository.findByToken("token-inexistente");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeListarTokensGuardados() {
        repository.save(new RefreshToken(null, "refresh-a", "usuario-a", new Date()));
        repository.save(new RefreshToken(null, "refresh-b", "usuario-b", new Date()));

        assertTrue(repository.findAll().size() >= 2);
    }
}
