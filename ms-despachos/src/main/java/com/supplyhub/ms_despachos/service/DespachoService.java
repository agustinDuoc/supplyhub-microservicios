package com.supplyhub.ms_despachos.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.supplyhub.ms_despachos.dto.*;
import com.supplyhub.ms_despachos.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_despachos.model.Despacho;
import com.supplyhub.ms_despachos.repository.DespachoRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DespachoService {

    private final DespachoRepository repository;
    private final WebClient webClient;

    public DespachoService(DespachoRepository repository, WebClient.Builder builder) {
        this.repository = repository;
        this.webClient = builder.build();
    }

    public List<DespachoResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertir)
                .toList();
    }

    public DespachoResponseDTO buscarPorId(Long id) {
        Despacho despacho = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Despacho no encontrado con id: " + id));

        return convertir(despacho);
    }

    public DespachoResponseDTO guardar(DespachoRequestDTO dto) {
        OrdenCompraDTO orden = obtenerOrdenCompra(dto.getIdOrdenCompra());
        PagoDTO pago = obtenerPago(dto.getIdPago());

        if (!"APROBADO".equalsIgnoreCase(pago.getEstadoPago())) {
            throw new RuntimeException("No se puede crear despacho porque el pago no está aprobado");
        }

        Despacho despacho = new Despacho();
        despacho.setIdOrdenCompra(orden.getId());
        despacho.setIdPago(pago.getId());
        despacho.setDireccionEnvio(dto.getDireccionEnvio());
        despacho.setEstadoDespacho(dto.getEstadoDespacho());
        despacho.setFechaEnvio(LocalDate.now());
        despacho.setFechaEntrega(dto.getFechaEntrega());

        Despacho guardado = repository.save(despacho);

        log.info("Despacho creado con id {}", guardado.getId());

        return convertir(guardado);
    }

    public DespachoResponseDTO actualizar(Long id, DespachoRequestDTO dto) {
        Despacho despacho = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Despacho no encontrado con id: " + id));

        OrdenCompraDTO orden = obtenerOrdenCompra(dto.getIdOrdenCompra());
        PagoDTO pago = obtenerPago(dto.getIdPago());

        if (!"APROBADO".equalsIgnoreCase(pago.getEstadoPago())) {
            throw new RuntimeException("No se puede actualizar despacho porque el pago no está aprobado");
        }

        despacho.setIdOrdenCompra(orden.getId());
        despacho.setIdPago(pago.getId());
        despacho.setDireccionEnvio(dto.getDireccionEnvio());
        despacho.setEstadoDespacho(dto.getEstadoDespacho());
        despacho.setFechaEntrega(dto.getFechaEntrega());

        return convertir(repository.save(despacho));
    }

    public void eliminar(Long id) {
        Despacho despacho = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Despacho no encontrado con id: " + id));

        repository.delete(despacho);
    }

    private DespachoResponseDTO convertir(Despacho despacho) {
        OrdenCompraDTO orden = obtenerOrdenCompra(despacho.getIdOrdenCompra());
        PagoDTO pago = obtenerPago(despacho.getIdPago());

        return DespachoResponseDTO.builder()
                .id(despacho.getId())
                .ordenCompra(orden)
                .pago(pago)
                .direccionEnvio(despacho.getDireccionEnvio())
                .estadoDespacho(despacho.getEstadoDespacho())
                .fechaEnvio(despacho.getFechaEnvio())
                .fechaEntrega(despacho.getFechaEntrega())
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

    private PagoDTO obtenerPago(Long idPago) {
        try {
            ExternalApiResponse<PagoDTO> response = webClient.get()
                    .uri("http://localhost:8089/api/v1/pagos/" + idPago)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ExternalApiResponse<PagoDTO>>() {})
                    .block();

            return response.getData();

        } catch (Exception e) {
            log.warn("No se pudo obtener pago id {}", idPago);
            throw new RecursoNoEncontradoException("Pago no encontrado con id: " + idPago);
        }
    }
}
