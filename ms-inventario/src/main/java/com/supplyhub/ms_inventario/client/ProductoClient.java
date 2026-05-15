package com.supplyhub.ms_inventario.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;

import com.supplyhub.ms_inventario.dto.ApiResponse;
import com.supplyhub.ms_inventario.dto.ProductoDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductoClient {

    private final WebClient webClient;

    private final String BASE_URL = "http://localhost:8089/api/v1/productos/";

    public ProductoDTO obtenerProducto(Long idProducto, String token) {

        ApiResponse<ProductoDTO> response = webClient.get()
                .uri(BASE_URL + idProducto)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<ProductoDTO>>() {})
                .block();

        return response != null ? response.getData() : null;
    }
}