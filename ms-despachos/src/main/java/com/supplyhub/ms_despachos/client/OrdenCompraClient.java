package com.supplyhub.ms_despachos.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.supplyhub.ms_despachos.dto.ApiResponse;
import com.supplyhub.ms_despachos.dto.OrdenCompraDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrdenCompraClient {

    private final WebClient webClient;

    private static final String BASE_URL = "http://ms-ordenes-compra/api/v1/ordenes-compra/";

    public OrdenCompraDTO obtenerOrdenCompra(Long idOrdenCompra, String token) {
        WebClient.RequestHeadersSpec<?> request = webClient.get().uri(BASE_URL + idOrdenCompra);

        if (token != null && !token.isBlank()) {
            request = request.header("Authorization", token);
        }

        ApiResponse<OrdenCompraDTO> response = request.retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<OrdenCompraDTO>>() {})
                .block();

        return response != null ? response.getData() : null;
    }
}
