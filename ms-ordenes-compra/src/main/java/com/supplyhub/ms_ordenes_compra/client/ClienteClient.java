package com.supplyhub.ms_ordenes_compra.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.supplyhub.ms_ordenes_compra.dto.ApiResponse;
import com.supplyhub.ms_ordenes_compra.dto.ClienteDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClienteClient {

    private final WebClient webClient;

    private final String BASE_URL = "http://localhost:8086/api/v1/clientes/";

    public ClienteDTO obtenerCliente(Long idCliente, String token) {

        ApiResponse<ClienteDTO> response = webClient.get()
                .uri(BASE_URL + idCliente)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<ClienteDTO>>() {})
                .block();

        return response != null ? response.getData() : null;
    }
}
