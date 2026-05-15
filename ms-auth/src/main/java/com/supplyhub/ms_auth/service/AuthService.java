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

    private final UsuarioRepository usuarioRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {

        if (usuarioRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("El username ya existe");
        }

        Usuario user = new Usuario();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(normalizarRol(req.getRole()));

        usuarioRepo.save(user);

        String access = jwtUtil.generarToken(user.getUsername(), user.getRole());

        String refresh = generarRefreshToken(user.getUsername());

        return new AuthResponse(access, refresh);
    }

    public AuthResponse login(LoginRequest req) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsername(),
                        req.getPassword())
        );

        Usuario user = usuarioRepo.findByUsername(req.getUsername())
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
                || !jwtUtil.esRefreshToken(refreshToken)) {

            throw new RuntimeException("Refresh token inválido");
        }

        Usuario user = usuarioRepo.findByUsername(token.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String newAccess = jwtUtil.generarToken(
                user.getUsername(),
                user.getRole()
        );

        return new AuthResponse(newAccess, refreshToken);
    }

    private String normalizarRol(String role) {
        if (role == null || role.isBlank()) {
            return "CLIENTE";
        }
        String limpio = role.trim().toUpperCase();
        if (limpio.startsWith("ROLE_")) {
            limpio = limpio.substring(5);
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
