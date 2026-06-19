package com.supplyhub.ms_auth.service;

import java.util.Date;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.supplyhub.ms_auth.dto.*;
import com.supplyhub.ms_auth.model.RefreshToken;
import com.supplyhub.ms_auth.model.Usuario;
import com.supplyhub.ms_auth.repository.RefreshTokenRepository;
import com.supplyhub.ms_auth.repository.UsuarioRepository;
import com.supplyhub.ms_auth.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_CLIENTE = "CLIENTE";

    private final UsuarioRepository usuarioRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        String username = normalizarUsername(req.getUsername());
        validarPassword(req.getPassword());
        String role = normalizarRol(req.getRole());

        if (usuarioRepo.findByUsername(username).isPresent()) {
            throw new RuntimeException("El username ya existe");
        }

        Usuario user = new Usuario();
        user.setUsername(username);
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(role);

        usuarioRepo.save(user);

        String access = jwtUtil.generarToken(user.getUsername(), user.getRole());
        String refresh = generarRefreshToken(user.getUsername());

        log.info("Usuario registrado: {} con rol {}", user.getUsername(), user.getRole());

        return new AuthResponse(access, refresh);
    }

    public AuthResponse login(LoginRequest req) {
        String username = normalizarUsername(req.getUsername());

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        req.getPassword())
        );

        Usuario user = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String access = jwtUtil.generarToken(
                user.getUsername(),
                user.getRole()
        );

        String refresh = generarRefreshToken(user.getUsername());

        log.info("Login exitoso: {}", user.getUsername());

        return new AuthResponse(access, refresh);
    }

    public AuthResponse refresh(String refreshToken) {
        RefreshToken token = refreshRepo.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh inválido"));

        if (!jwtUtil.esValido(refreshToken)
                || !jwtUtil.esRefreshToken(refreshToken)
                || token.getExpiryDate().before(new Date())) {
            throw new RuntimeException("Refresh token inválido o expirado");
        }

        Usuario user = usuarioRepo.findByUsername(token.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String newAccess = jwtUtil.generarToken(
                user.getUsername(),
                user.getRole()
        );

        return new AuthResponse(newAccess, refreshToken);
    }

    private String normalizarUsername(String username) {
        if (username == null) {
            String mensaje = "El username es obligatorio";
            throw new RuntimeException(mensaje);
        }
        if (username.isBlank()) {
            String mensaje = "El username no puede estar vacío";
            throw new RuntimeException(mensaje);
        }
        if (username.length() > 80) {
            String mensaje = "El username no puede superar 80 caracteres";
            throw new RuntimeException(mensaje);
        }
        return username.trim().toLowerCase();
    }

    private void validarPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }
    }

    private String normalizarRol(String role) {
        if (role == null || role.isBlank()) {
            return ROLE_CLIENTE;
        }
        String limpio = role.trim().toUpperCase();
        if (limpio.startsWith("ROLE_")) {
            limpio = limpio.substring(5);
        }
        if (!ROLE_ADMIN.equals(limpio) && !ROLE_CLIENTE.equals(limpio)) {
            throw new RuntimeException("El rol debe ser ADMIN o CLIENTE");
        }
        return limpio;
    }

    private String generarRefreshToken(String username) {
        String token = jwtUtil.generarRefreshTokenJwt(username);

        RefreshToken rt = new RefreshToken();

        rt.setToken(token);
        rt.setUsername(username);
        rt.setExpiryDate(
                new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24)
        );

        refreshRepo.save(rt);

        return token;
    }
}
