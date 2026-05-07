package com.supplyhub.ms_ordenes_compra.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.supplyhub.ms_ordenes_compra.dto.*;
import com.supplyhub.ms_ordenes_compra.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_ordenes_compra.model.OrdenCompra;
import com.supplyhub.ms_ordenes_compra.repository.OrdenCompraRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenCompraService {

    private final OrdenCompraRepository repository;
    private final WebClient webClient;

    public OrdenCompraService(OrdenCompraRepository repository, WebClient.Builder builder) {
        this.repository = repository;
        this.webClient = builder.build();
    }

    public List<OrdenCompraResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertir)
                .toList();
    }

    public OrdenCompraResponseDTO buscarPorId(Long id) {
        OrdenCompra orden = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden de compra no encontrada con id: " + id));

        return convertir(orden);
    }

    public OrdenCompraResponseDTO guardar(OrdenCompraRequestDTO dto) {
        ClienteDTO cliente = obtenerCliente(dto.getIdCliente());
        InventarioDTO inventario = obtenerInventario(dto.getIdInventario());

        if (inventario.getStockDisponible() < dto.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para generar la orden");
        }

        OrdenCompra orden = new OrdenCompra();
        orden.setIdCliente(cliente.getId());
        orden.setIdInventario(inventario.getId());
        orden.setCantidad(dto.getCantidad());
        orden.setTotal(dto.getTotal());
        orden.setEstado(dto.getEstado());
        orden.setFechaOrden(LocalDate.now());

        OrdenCompra guardada = repository.save(orden);

        log.info("Orden de compra creada con id {}", guardada.getId());

        return convertir(guardada);
    }

    public OrdenCompraResponseDTO actualizar(Long id, OrdenCompraRequestDTO dto) {
        OrdenCompra orden = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden de compra no encontrada con id: " + id));

        ClienteDTO cliente = obtenerCliente(dto.getIdCliente());
        InventarioDTO inventario = obtenerInventario(dto.getIdInventario());

        if (inventario.getStockDisponible() < dto.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para actualizar la orden");
        }

        orden.setIdCliente(cliente.getId());
        orden.setIdInventario(inventario.getId());
        orden.setCantidad(dto.getCantidad());
        orden.setTotal(dto.getTotal());
        orden.setEstado(dto.getEstado());

        return convertir(repository.save(orden));
    }

    public void eliminar(Long id) {
        OrdenCompra orden = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden de compra no encontrada con id: " + id));

        repository.delete(orden);
    }

    private OrdenCompraResponseDTO convertir(OrdenCompra orden) {
        ClienteDTO cliente = obtenerCliente(orden.getIdCliente());
        InventarioDTO inventario = obtenerInventario(orden.getIdInventario());

        return OrdenCompraResponseDTO.builder()
                .id(orden.getId())
                .cliente(cliente)
                .inventario(inventario)
                .cantidad(orden.getCantidad())
                .total(orden.getTotal())
                .estado(orden.getEstado())
                .fechaOrden(orden.getFechaOrden())
                .build();
    }

    private ClienteDTO obtenerCliente(Long idCliente) {
        try {
            ExternalApiResponse<ClienteDTO> response = webClient.get()
                    .uri("http://localhost:8081/api/v1/clientes/" + idCliente)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ExternalApiResponse<ClienteDTO>>() {})
                    .block();

            return response.getData();

        } catch (Exception e) {
            log.warn("No se pudo obtener cliente id {}", idCliente);
            throw new RecursoNoEncontradoException("Cliente no encontrado con id: " + idCliente);
        }
    }

    private InventarioDTO obtenerInventario(Long idInventario) {
        try {
            ExternalApiResponse<InventarioDTO> response = webClient.get()
                    .uri("http://localhost:8086/api/v1/inventario/" + idInventario)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ExternalApiResponse<InventarioDTO>>() {})
                    .block();

            return response.getData();

        } catch (Exception e) {
            log.warn("No se pudo obtener inventario id {}", idInventario);
            throw new RecursoNoEncontradoException("Inventario no encontrado con id: " + idInventario);
        }
    }
}
