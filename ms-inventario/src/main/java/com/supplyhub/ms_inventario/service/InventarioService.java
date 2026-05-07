package com.supplyhub.ms_inventario.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.supplyhub.ms_inventario.dto.*;
import com.supplyhub.ms_inventario.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_inventario.model.Inventario;
import com.supplyhub.ms_inventario.repository.InventarioRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventarioService {

    private final InventarioRepository repository;
    private final WebClient webClient;

    public InventarioService(InventarioRepository repository, WebClient.Builder builder) {
        this.repository = repository;
        this.webClient = builder.build();
    }

    public List<InventarioResponseDTO> listar() {
        return repository.findAll().stream()
                .map(this::convertir)
                .toList();
    }

public InventarioResponseDTO guardar(InventarioRequestDTO dto) {
    obtenerProducto(dto.getIdProducto());
    Inventario inv = new Inventario();
    inv.setIdProducto(dto.getIdProducto());
    inv.setStockDisponible(dto.getStockDisponible());
    inv.setStockMinimo(dto.getStockMinimo());
    inv.setUbicacion(dto.getUbicacion());
    inv.setEstado(dto.getEstado());

    return convertir(repository.save(inv));
}

    private InventarioResponseDTO convertir(Inventario inv) {
        ProductoDTO producto = obtenerProducto(inv.getIdProducto());

        return InventarioResponseDTO.builder()
                .id(inv.getId())
                .producto(producto)
                .stockDisponible(inv.getStockDisponible())
                .stockMinimo(inv.getStockMinimo())
                .ubicacion(inv.getUbicacion())
                .estado(inv.getEstado())
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
            throw new RecursoNoEncontradoException("Producto no encontrado con id: " + idProducto);
        }
    }
    
    public InventarioResponseDTO buscarPorId(Long id) {
    Inventario inv = repository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                    "Inventario no encontrado con id: " + id
            ));

    return convertir(inv);
}
}
