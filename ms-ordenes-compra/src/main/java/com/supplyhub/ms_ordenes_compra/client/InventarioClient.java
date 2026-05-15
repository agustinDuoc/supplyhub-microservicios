package com.supplyhub.ms_ordenes_compra.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.supplyhub.ms_ordenes_compra.dto.ApiResponse;
import com.supplyhub.ms_ordenes_compra.dto.InventarioDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventarioClient {

    private final WebClient webClient;

    private final String BASE_URL = "http://localhost:8091/api/v1/inventario/";

    public InventarioDTO obtenerInventario(Long idInventario, String token) {

        ApiResponse<InventarioDTO> response = webClient.get()
                .uri(BASE_URL + idInventario)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<InventarioDTO>>() {})
                .block();

        return response != null ? response.getData() : null;
    }
}
