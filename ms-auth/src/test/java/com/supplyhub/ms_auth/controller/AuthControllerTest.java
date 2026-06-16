package com.supplyhub.ms_auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplyhub.ms_auth.dto.AuthResponse;
import com.supplyhub.ms_auth.dto.LoginRequest;
import com.supplyhub.ms_auth.dto.RefreshRequest;
import com.supplyhub.ms_auth.dto.RegisterRequest;
import com.supplyhub.ms_auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService service;

    private AuthResponse authResponseEjemplo() {
        return new AuthResponse("access-token", "refresh-token");
    }

    @Test
    void debeRegistrarUsuario() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("usuario");
        req.setPassword("password123");
        req.setRole("CLIENTE");

        when(service.register(any(RegisterRequest.class))).thenReturn(authResponseEjemplo());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuario registrado"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));
    }

    @Test
    void debeIniciarSesion() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("usuario");
        req.setPassword("password123");

        when(service.login(any(LoginRequest.class))).thenReturn(authResponseEjemplo());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login exitoso"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));
    }

    @Test
    void debeRenovarToken() throws Exception {
        RefreshRequest req = new RefreshRequest();
        req.setRefreshToken("refresh-token");

        when(service.refresh("refresh-token")).thenReturn(authResponseEjemplo());

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token renovado"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));
    }
}
