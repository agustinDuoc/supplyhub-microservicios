package com.supplyhub.ms_productos.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.supplyhub.ms_productos.dto.ApiResponse;
import com.supplyhub.ms_productos.dto.CategoriaDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoriaClient {

    private final WebClient webClient;

    private static final String BASE_URL = "http://ms-categorias/api/v1/categorias/";

    public CategoriaDTO obtenerCategoria(Long idCategoria, String token) {
        WebClient.RequestHeadersSpec<?> request = webClient.get().uri(BASE_URL + idCategoria);

        if (token != null && !token.isBlank()) {
            request = request.header("Authorization", token);
        }

        ApiResponse<CategoriaDTO> response = request.retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CategoriaDTO>>() {})
                .block();

        return response != null ? response.getData() : null;
    }
}
