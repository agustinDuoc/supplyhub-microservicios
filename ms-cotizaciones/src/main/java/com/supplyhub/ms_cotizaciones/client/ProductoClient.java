package com.supplyhub.ms_cotizaciones.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.supplyhub.ms_cotizaciones.dto.ApiResponse;
import com.supplyhub.ms_cotizaciones.dto.ProductoDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductoClient {

    private final WebClient webClient;

    private static final String BASE_URL = "http://ms-productos/api/v1/productos/";

    public ProductoDTO obtenerProducto(Long idProducto, String token) {
        WebClient.RequestHeadersSpec<?> request = webClient.get().uri(BASE_URL + idProducto);

        if (token != null && !token.isBlank()) {
            request = request.header("Authorization", token);
        }

        ApiResponse<ProductoDTO> response = request.retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<ProductoDTO>>() {})
                .block();

        return response != null ? response.getData() : null;
    }
}
