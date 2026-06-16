package com.supplyhub.ms_despachos.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.supplyhub.ms_despachos.dto.ApiResponse;
import com.supplyhub.ms_despachos.dto.PagoDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PagoClient {

    private final WebClient webClient;

    private static final String BASE_URL = "http://ms-pagos/api/v1/pagos/";

    public PagoDTO obtenerPago(Long idPago, String token) {
        WebClient.RequestHeadersSpec<?> request = webClient.get().uri(BASE_URL + idPago);

        if (token != null && !token.isBlank()) {
            request = request.header("Authorization", token);
        }

        ApiResponse<PagoDTO> response = request.retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<PagoDTO>>() {})
                .block();

        return response != null ? response.getData() : null;
    }
}
