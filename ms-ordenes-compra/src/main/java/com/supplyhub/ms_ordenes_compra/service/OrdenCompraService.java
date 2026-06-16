package com.supplyhub.ms_ordenes_compra.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_APROBADA = "APROBADA";
    private static final String ESTADO_CANCELADA = "CANCELADA";

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
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden de compra no encontrada con id: " + id));

        return convertir(orden, token);
    }

    public OrdenCompraResponseDTO guardar(OrdenCompraRequestDTO dto, String token) {
        ClienteDTO cliente = obtenerClienteActivo(dto.getIdCliente(), token);
        InventarioDTO inventario = obtenerInventarioDisponible(dto.getIdInventario(), token);

        validarStock(inventario, dto.getCantidad(), "generar");

        OrdenCompra orden = new OrdenCompra();
        orden.setIdCliente(cliente.getId());
        orden.setIdInventario(inventario.getId());
        orden.setCantidad(dto.getCantidad());
        orden.setTotal(calcularTotal(inventario, dto.getCantidad()));
        orden.setEstado(normalizarEstado(dto.getEstado()));
        orden.setFechaOrden(LocalDate.now());

        OrdenCompra guardada = repository.save(orden);

        log.info("Orden de compra creada con id {}", guardada.getId());

        return convertir(guardada, token);
    }

    public OrdenCompraResponseDTO actualizar(Long id, OrdenCompraRequestDTO dto, String token) {
        OrdenCompra orden = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden de compra no encontrada con id: " + id));

        ClienteDTO cliente = obtenerClienteActivo(dto.getIdCliente(), token);
        InventarioDTO inventario = obtenerInventarioDisponible(dto.getIdInventario(), token);

        validarStock(inventario, dto.getCantidad(), "actualizar");

        orden.setIdCliente(cliente.getId());
        orden.setIdInventario(inventario.getId());
        orden.setCantidad(dto.getCantidad());
        orden.setTotal(calcularTotal(inventario, dto.getCantidad()));
        orden.setEstado(normalizarEstado(dto.getEstado()));

        OrdenCompra actualizada = repository.save(orden);

        log.info("Orden de compra actualizada con id {}", actualizada.getId());

        return convertir(actualizada, token);
    }

    public void eliminar(Long id) {
        OrdenCompra orden = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden de compra no encontrada con id: " + id));

        if (ESTADO_APROBADA.equalsIgnoreCase(orden.getEstado())) {
            throw new RuntimeException("No se puede eliminar una orden aprobada");
        }

        repository.delete(orden);

        log.info("Orden de compra eliminada con id {}", id);
    }

    private OrdenCompraResponseDTO convertir(OrdenCompra orden, String token) {
        ClienteDTO cliente = obtenerCliente(orden.getIdCliente(), token);
        InventarioDTO inventario = obtenerInventario(orden.getIdInventario(), token);

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

    private ClienteDTO obtenerClienteActivo(Long idCliente, String token) {
        ClienteDTO cliente = obtenerCliente(idCliente, token);
        if (!"ACTIVO".equalsIgnoreCase(cliente.getEstado())) {
            throw new RuntimeException("No se puede crear una orden para un cliente inactivo");
        }
        return cliente;
    }

    private InventarioDTO obtenerInventarioDisponible(Long idInventario, String token) {
        InventarioDTO inventario = obtenerInventario(idInventario, token);
        if ("SIN_STOCK".equalsIgnoreCase(inventario.getEstado())) {
            throw new RuntimeException("No se puede crear una orden con inventario sin stock");
        }
        return inventario;
    }

    private ClienteDTO obtenerCliente(Long idCliente, String token) {
        try {
            ClienteDTO cliente = clienteClient.obtenerCliente(idCliente, token);
            if (cliente == null) {
                throw new RecursoNoEncontradoException("Cliente no encontrado con id: " + idCliente);
            }
            return cliente;
        } catch (RecursoNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            log.warn("No se pudo obtener cliente id {}", idCliente);
            throw new RecursoNoEncontradoException("Cliente no encontrado con id: " + idCliente);
        }
    }

    private InventarioDTO obtenerInventario(Long idInventario, String token) {
        try {
            InventarioDTO inventario = inventarioClient.obtenerInventario(idInventario, token);
            if (inventario == null) {
                throw new RecursoNoEncontradoException("Inventario no encontrado con id: " + idInventario);
            }
            return inventario;
        } catch (RecursoNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            log.warn("No se pudo obtener inventario id {}", idInventario);
            throw new RecursoNoEncontradoException("Inventario no encontrado con id: " + idInventario);
        }
    }

    private void validarStock(InventarioDTO inventario, Integer cantidad, String accion) {
        if (inventario.getStockDisponible() < cantidad) {
            throw new RuntimeException("Stock insuficiente para " + accion + " la orden");
        }
    }

    private Integer calcularTotal(InventarioDTO inventario, Integer cantidad) {
        Integer precioProducto = extraerPrecioProducto(inventario.getProducto());
        return precioProducto * cantidad;
    }

    @SuppressWarnings("unchecked")
    private Integer extraerPrecioProducto(Object producto) {
        if (producto instanceof Map<?, ?> map) {
            Object precio = map.get("precio");
            if (precio instanceof Integer valor) {
                return valor;
            }
            if (precio instanceof Number numero) {
                return numero.intValue();
            }
        }
        throw new RuntimeException("No se pudo calcular el total porque el inventario no contiene precio del producto");
    }

    private String normalizarEstado(String estado) {
        String limpio = estado.trim().toUpperCase();
        if (!ESTADO_PENDIENTE.equals(limpio) && !ESTADO_APROBADA.equals(limpio) && !ESTADO_CANCELADA.equals(limpio)) {
            throw new RuntimeException("El estado de la orden debe ser PENDIENTE, APROBADA o CANCELADA");
        }
        return limpio;
    }
}
