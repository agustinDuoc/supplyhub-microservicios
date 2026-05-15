package com.supplyhub.ms_ordenes_compra.service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.supplyhub.ms_ordenes_compra.client.ClienteClient;
import com.supplyhub.ms_ordenes_compra.client.InventarioClient;
import com.supplyhub.ms_ordenes_compra.dto.ClienteDTO;
import com.supplyhub.ms_ordenes_compra.dto.InventarioDTO;
import com.supplyhub.ms_ordenes_compra.dto.OrdenCompraRequestDTO;
import com.supplyhub.ms_ordenes_compra.dto.OrdenCompraResponseDTO;
import com.supplyhub.ms_ordenes_compra.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_ordenes_compra.model.OrdenCompra;
import com.supplyhub.ms_ordenes_compra.repository.OrdenCompraRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdenCompraService {

    private final OrdenCompraRepository repository;
    private final ClienteClient clienteClient;
    private final InventarioClient inventarioClient;

    public List<OrdenCompraResponseDTO> listar(String token) {

        List<OrdenCompra> ordenes = repository.findAll();
        List<OrdenCompraResponseDTO> respuesta = new ArrayList<>();

        for (OrdenCompra orden : ordenes) {
            respuesta.add(convertir(orden, token));
        }

        return respuesta;
    }

    public OrdenCompraResponseDTO buscarPorId(Long id, String token) {

        OrdenCompra orden = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Orden de compra no encontrada con id: " + id
                ));

        return convertir(orden, token);
    }

    public OrdenCompraResponseDTO guardar(OrdenCompraRequestDTO dto, String token) {

        ClienteDTO cliente = clienteClient.obtenerCliente(dto.getIdCliente(), token);
        InventarioDTO inventario = inventarioClient.obtenerInventario(dto.getIdInventario(), token);

        if (cliente == null) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con id: " + dto.getIdCliente());
        }

        if (inventario == null) {
            throw new RecursoNoEncontradoException("Inventario no encontrado con id: " + dto.getIdInventario());
        }

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

        return convertir(guardada, token);
    }

    public OrdenCompraResponseDTO actualizar(Long id, OrdenCompraRequestDTO dto, String token) {

        OrdenCompra orden = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Orden de compra no encontrada con id: " + id
                ));

        ClienteDTO cliente = clienteClient.obtenerCliente(dto.getIdCliente(), token);
        InventarioDTO inventario = inventarioClient.obtenerInventario(dto.getIdInventario(), token);

        if (cliente == null) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con id: " + dto.getIdCliente());
        }

        if (inventario == null) {
            throw new RecursoNoEncontradoException("Inventario no encontrado con id: " + dto.getIdInventario());
        }

        if (inventario.getStockDisponible() < dto.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para actualizar la orden");
        }

        orden.setIdCliente(cliente.getId());
        orden.setIdInventario(inventario.getId());
        orden.setCantidad(dto.getCantidad());
        orden.setTotal(dto.getTotal());
        orden.setEstado(dto.getEstado());

        OrdenCompra actualizada = repository.save(orden);

        log.info("Orden de compra actualizada con id {}", actualizada.getId());

        return convertir(actualizada, token);
    }

    public void eliminar(Long id) {

        OrdenCompra orden = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Orden de compra no encontrada con id: " + id
                ));

        repository.delete(orden);

        log.info("Orden de compra eliminada con id {}", id);
    }

    private OrdenCompraResponseDTO convertir(OrdenCompra orden, String token) {

        ClienteDTO cliente = clienteClient.obtenerCliente(orden.getIdCliente(), token);
        InventarioDTO inventario = inventarioClient.obtenerInventario(orden.getIdInventario(), token);

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
}
