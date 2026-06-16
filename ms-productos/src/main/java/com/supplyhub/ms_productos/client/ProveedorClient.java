package com.supplyhub.ms_productos.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.supplyhub.ms_productos.dto.ApiResponse;
import com.supplyhub.ms_productos.dto.ProveedorDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProveedorClient {

    private final WebClient webClient;

    private static final String BASE_URL = "http://ms-proveedores/api/v1/proveedores/";

    public ProveedorDTO obtenerProveedor(Long idProveedor, String token) {
        WebClient.RequestHeadersSpec<?> request = webClient.get().uri(BASE_URL + idProveedor);

        if (token != null && !token.isBlank()) {
            request = request.header("Authorization", token);
        }

        ApiResponse<ProveedorDTO> response = request.retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<ProveedorDTO>>() {})
                .block();

        return response != null ? response.getData() : null;
    }
}
