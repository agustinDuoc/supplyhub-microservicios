package com.supplyhub.ms_cotizaciones.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.supplyhub.ms_cotizaciones.dto.*;
import com.supplyhub.ms_cotizaciones.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_cotizaciones.model.Cotizacion;
import com.supplyhub.ms_cotizaciones.repository.CotizacionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CotizacionService {

    private final CotizacionRepository repository;
    private final WebClient webClient;

    public CotizacionService(CotizacionRepository repository, WebClient.Builder builder) {
        this.repository = repository;
        this.webClient = builder.build();
    }

    public List<CotizacionResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertir)
                .toList();
    }

    public CotizacionResponseDTO buscarPorId(Long id) {
        Cotizacion cotizacion = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cotización no encontrada con id: " + id));

        return convertir(cotizacion);
    }

    public CotizacionResponseDTO guardar(CotizacionRequestDTO dto) {
        ProductoDTO producto = obtenerProducto(dto.getIdProducto());

        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setIdProducto(producto.getId());
        cotizacion.setCantidad(dto.getCantidad());
        cotizacion.setTotal(dto.getTotal());
        cotizacion.setEstado(dto.getEstado());

        Cotizacion guardada = repository.save(cotizacion);

        log.info("Cotización creada con id {}", guardada.getId());

        return convertir(guardada);
    }

    public CotizacionResponseDTO actualizar(Long id, CotizacionRequestDTO dto) {
        Cotizacion cotizacion = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cotización no encontrada con id: " + id));

        ProductoDTO producto = obtenerProducto(dto.getIdProducto());

        cotizacion.setIdProducto(producto.getId());
        cotizacion.setCantidad(dto.getCantidad());
        cotizacion.setTotal(dto.getTotal());
        cotizacion.setEstado(dto.getEstado());

        return convertir(repository.save(cotizacion));
    }

    public void eliminar(Long id) {
        Cotizacion cotizacion = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cotización no encontrada con id: " + id));

        repository.delete(cotizacion);
    }

    private CotizacionResponseDTO convertir(Cotizacion cotizacion) {
        ProductoDTO producto = obtenerProducto(cotizacion.getIdProducto());

        return CotizacionResponseDTO.builder()
                .id(cotizacion.getId())
                .producto(producto)
                .cantidad(cotizacion.getCantidad())
                .total(cotizacion.getTotal())
                .estado(cotizacion.getEstado())
                .build();
    }

    private ProductoDTO obtenerProducto(Long idProducto) {
        try {
            ExternalApiResponse<ProductoDTO> response = webClient.get()
                    .uri("http://localhost:8085/api/v1/productos/" + idProducto)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ExternalApiResponse<ProductoDTO>>() {})
                    .block();

            return response.getData();

        } catch (Exception e) {
            log.warn("No se pudo obtener producto id {}", idProducto);
            throw new RecursoNoEncontradoException("Producto no encontrado con id: " + idProducto);
        }
    }
}
