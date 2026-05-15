package com.supplyhub.ms_inventario.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.supplyhub.ms_inventario.client.ProductoClient;
import com.supplyhub.ms_inventario.dto.InventarioRequestDTO;
import com.supplyhub.ms_inventario.dto.InventarioResponseDTO;
import com.supplyhub.ms_inventario.dto.ProductoDTO;
import com.supplyhub.ms_inventario.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_inventario.model.Inventario;
import com.supplyhub.ms_inventario.repository.InventarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {

    private final InventarioRepository repository;
    private final ProductoClient productoClient;

    public List<InventarioResponseDTO> listar(String token) {
        List<Inventario> inventarios = repository.findAll();
        List<InventarioResponseDTO> respuesta = new ArrayList<>();

        for (Inventario inv : inventarios) {
            respuesta.add(convertir(inv, token));
        }

        return respuesta;
    }

    public InventarioResponseDTO guardar(InventarioRequestDTO dto, String token) {
        ProductoDTO producto = productoClient.obtenerProducto(dto.getIdProducto(), token);

        if (producto == null) {
            throw new RecursoNoEncontradoException("Producto no encontrado con id: " + dto.getIdProducto());
        }

        Inventario inv = new Inventario();
        inv.setIdProducto(dto.getIdProducto());
        inv.setStockDisponible(dto.getStockDisponible());
        inv.setStockMinimo(dto.getStockMinimo());
        inv.setUbicacion(dto.getUbicacion());
        inv.setEstado(dto.getEstado());

        Inventario guardado = repository.save(inv);
        log.info("Inventario creado para producto id: {}", dto.getIdProducto());
        return convertir(guardado, token);
    }

    public InventarioResponseDTO buscarPorId(Long id, String token) {
        Inventario inv = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado con id: " + id));

        return convertir(inv, token);
    }

    public InventarioResponseDTO actualizar(Long id, InventarioRequestDTO dto, String token) {
        Inventario inv = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado con id: " + id));

        ProductoDTO producto = productoClient.obtenerProducto(dto.getIdProducto(), token);
        if (producto == null) {
            throw new RecursoNoEncontradoException("Producto no encontrado con id: " + dto.getIdProducto());
        }

        inv.setIdProducto(dto.getIdProducto());
        inv.setStockDisponible(dto.getStockDisponible());
        inv.setStockMinimo(dto.getStockMinimo());
        inv.setUbicacion(dto.getUbicacion());
        inv.setEstado(dto.getEstado());

        return convertir(repository.save(inv), token);
    }

    public void eliminar(Long id) {
        Inventario inv = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado con id: " + id));
        repository.delete(inv);
    }

    private InventarioResponseDTO convertir(Inventario inv, String token) {
        ProductoDTO producto = productoClient.obtenerProducto(inv.getIdProducto(), token);

        InventarioResponseDTO response = new InventarioResponseDTO();
        response.setId(inv.getId());
        response.setProducto(producto);
        response.setStockDisponible(inv.getStockDisponible());
        response.setStockMinimo(inv.getStockMinimo());
        response.setUbicacion(inv.getUbicacion());
        response.setEstado(inv.getEstado());

        return response;
    }
}
