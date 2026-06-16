package com.supplyhub.ms_auth.service;

import com.supplyhub.ms_auth.dto.AuthResponse;
import com.supplyhub.ms_auth.dto.LoginRequest;
import com.supplyhub.ms_auth.dto.RegisterRequest;
import com.supplyhub.ms_auth.model.RefreshToken;
import com.supplyhub.ms_auth.model.Usuario;
import com.supplyhub.ms_auth.repository.RefreshTokenRepository;
import com.supplyhub.ms_auth.repository.UsuarioRepository;
import com.supplyhub.ms_auth.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepo;

    @Mock
    private RefreshTokenRepository refreshRepo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService service;

    @Test
    void deberiaRegistrarUsuarioCorrectamente() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("Usuario");
        req.setPassword("password123");
        req.setRole("CLIENTE");

        when(usuarioRepo.findByUsername("usuario")).thenReturn(Optional.empty());
        when(encoder.encode("password123")).thenReturn("encoded-password");
        when(usuarioRepo.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtUtil.generarToken("usuario", "CLIENTE")).thenReturn("access-token");
        when(jwtUtil.generarRefreshTokenJwt("usuario")).thenReturn("refresh-token");
        when(refreshRepo.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse resultado = service.register(req);

        assertEquals("access-token", resultado.getAccessToken());
        assertEquals("refresh-token", resultado.getRefreshToken());
        verify(usuarioRepo).save(any(Usuario.class));
        verify(refreshRepo).save(any(RefreshToken.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoUsernameYaExiste() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("existente");
        req.setPassword("password123");
        req.setRole("CLIENTE");

        when(usuarioRepo.findByUsername("existente")).thenReturn(Optional.of(new Usuario()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.register(req));
        assertTrue(ex.getMessage().contains("username ya existe"));
        verify(usuarioRepo, never()).save(any(Usuario.class));
    }

    @Test
    void deberiaIniciarSesionCorrectamente() {
        LoginRequest req = new LoginRequest();
        req.setUsername("usuario");
        req.setPassword("password123");

        Usuario user = Usuario.builder()
                .id(1L)
                .username("usuario")
                .password("encoded")
                .role("CLIENTE")
                .build();

        when(usuarioRepo.findByUsername("usuario")).thenReturn(Optional.of(user));
        when(jwtUtil.generarToken("usuario", "CLIENTE")).thenReturn("access-token");
        when(jwtUtil.generarRefreshTokenJwt("usuario")).thenReturn("refresh-token");
        when(refreshRepo.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse resultado = service.login(req);

        assertEquals("access-token", resultado.getAccessToken());
        verify(authManager).authenticate(any());
        verify(usuarioRepo).findByUsername("usuario");
    }

    @Test
    void deberiaRenovarTokenCorrectamente() {
        String refreshToken = "refresh-token";
        RefreshToken token = new RefreshToken();
        token.setToken(refreshToken);
        token.setUsername("usuario");
        token.setExpiryDate(new Date(System.currentTimeMillis() + 86400000));

        Usuario user = Usuario.builder()
                .id(1L)
                .username("usuario")
                .role("CLIENTE")
                .build();

        when(refreshRepo.findByToken(refreshToken)).thenReturn(Optional.of(token));
        when(jwtUtil.esValido(refreshToken)).thenReturn(true);
        when(jwtUtil.esRefreshToken(refreshToken)).thenReturn(true);
        when(usuarioRepo.findByUsername("usuario")).thenReturn(Optional.of(user));
        when(jwtUtil.generarToken("usuario", "CLIENTE")).thenReturn("new-access-token");

        AuthResponse resultado = service.refresh(refreshToken);

        assertEquals("new-access-token", resultado.getAccessToken());
        assertEquals(refreshToken, resultado.getRefreshToken());
        verify(refreshRepo).findByToken(refreshToken);
    }

    @Test
    void deberiaLanzarExcepcionConRefreshTokenInvalido() {
        when(refreshRepo.findByToken("invalid")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.refresh("invalid"));
        assertTrue(ex.getMessage().contains("Refresh inválido"));
    }

    @Test
    void deberiaRechazarPasswordCorta() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("usuario");
        req.setPassword("12345");
        req.setRole("CLIENTE");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.register(req));
        assertTrue(ex.getMessage().contains("6 caracteres"));
        verify(usuarioRepo, never()).save(any(Usuario.class));
    }
}
