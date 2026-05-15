package com.supplyhub.ms_pagos.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.supplyhub.ms_pagos.dto.*;
import com.supplyhub.ms_pagos.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_pagos.model.Pago;
import com.supplyhub.ms_pagos.repository.PagoRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PagoService {

    private final PagoRepository repository;
    private final WebClient webClient;

    public PagoService(PagoRepository repository, WebClient.Builder builder) {
        this.repository = repository;
        this.webClient = builder.build();
    }

    public List<PagoResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertir)
                .toList();
    }

    public PagoResponseDTO buscarPorId(Long id) {
        Pago pago = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado con id: " + id));

        return convertir(pago);
    }

    public PagoResponseDTO guardar(PagoRequestDTO dto) {
        OrdenCompraDTO orden = obtenerOrdenCompra(dto.getIdOrdenCompra());

        Pago pago = new Pago();
        pago.setIdOrdenCompra(orden.getId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstadoPago(dto.getEstadoPago());
        pago.setFechaPago(LocalDate.now());

        Pago guardado = repository.save(pago);

        log.info("Pago creado con id {}", guardado.getId());

        return convertir(guardado);
    }

    public PagoResponseDTO actualizar(Long id, PagoRequestDTO dto) {
        Pago pago = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado con id: " + id));

        OrdenCompraDTO orden = obtenerOrdenCompra(dto.getIdOrdenCompra());

        pago.setIdOrdenCompra(orden.getId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstadoPago(dto.getEstadoPago());

        return convertir(repository.save(pago));
    }

    public void eliminar(Long id) {
        Pago pago = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado con id: " + id));

        repository.delete(pago);
    }

    private PagoResponseDTO convertir(Pago pago) {
        OrdenCompraDTO orden = obtenerOrdenCompra(pago.getIdOrdenCompra());

        return PagoResponseDTO.builder()
                .id(pago.getId())
                .ordenCompra(orden)
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago())
                .estadoPago(pago.getEstadoPago())
                .fechaPago(pago.getFechaPago())
                .build();
    }

    private OrdenCompraDTO obtenerOrdenCompra(Long idOrdenCompra) {
        try {
            ExternalApiResponse<OrdenCompraDTO> response = webClient.get()
                    .uri("http://localhost:8088/api/v1/ordenes-compra/" + idOrdenCompra)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ExternalApiResponse<OrdenCompraDTO>>() {})
                    .block();

            return response.getData();

        } catch (Exception e) {
            log.warn("No se pudo obtener orden de compra id {}", idOrdenCompra);
            throw new RecursoNoEncontradoException("Orden de compra no encontrada con id: " + idOrdenCompra);
        }
    }
}
