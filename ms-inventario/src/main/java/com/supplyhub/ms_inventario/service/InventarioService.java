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

    private static final String ESTADO_DISPONIBLE = "DISPONIBLE";
    private static final String ESTADO_BAJO_STOCK = "BAJO_STOCK";
    private static final String ESTADO_SIN_STOCK = "SIN_STOCK";

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
        ProductoDTO producto = obtenerProducto(dto.getIdProducto(), token);
        validarProductoActivo(producto);
        validarStock(dto.getStockDisponible(), dto.getStockMinimo());

        repository.findByIdProducto(producto.getId()).ifPresent(existente -> {
            throw new RuntimeException("Ya existe inventario para el producto con id: " + producto.getId());
        });

        Inventario inv = new Inventario();
        inv.setIdProducto(producto.getId());
        inv.setStockDisponible(dto.getStockDisponible());
        inv.setStockMinimo(dto.getStockMinimo());
        inv.setUbicacion(dto.getUbicacion());
        inv.setEstado(calcularEstado(dto.getStockDisponible(), dto.getStockMinimo()));

        Inventario guardado = repository.save(inv);
        log.info("Inventario creado para producto id: {}", producto.getId());
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

        ProductoDTO producto = obtenerProducto(dto.getIdProducto(), token);
        validarProductoActivo(producto);
        validarStock(dto.getStockDisponible(), dto.getStockMinimo());

        repository.findByIdProducto(producto.getId()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new RuntimeException("Ya existe inventario para el producto con id: " + producto.getId());
            }
        });

        inv.setIdProducto(producto.getId());
        inv.setStockDisponible(dto.getStockDisponible());
        inv.setStockMinimo(dto.getStockMinimo());
        inv.setUbicacion(dto.getUbicacion());
        inv.setEstado(calcularEstado(dto.getStockDisponible(), dto.getStockMinimo()));

        return convertir(repository.save(inv), token);
    }

    public void eliminar(Long id) {
        Inventario inv = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado con id: " + id));
        repository.delete(inv);
    }

    private InventarioResponseDTO convertir(Inventario inv, String token) {
        ProductoDTO producto = obtenerProducto(inv.getIdProducto(), token);

        InventarioResponseDTO response = new InventarioResponseDTO();
        response.setId(inv.getId());
        response.setProducto(producto);
        response.setStockDisponible(inv.getStockDisponible());
        response.setStockMinimo(inv.getStockMinimo());
        response.setUbicacion(inv.getUbicacion());
        response.setEstado(inv.getEstado());

        return response;
    }

    private ProductoDTO obtenerProducto(Long idProducto, String token) {
        try {
            ProductoDTO producto = productoClient.obtenerProducto(idProducto, token);
            if (producto == null) {
                throw new RecursoNoEncontradoException("Producto no encontrado con id: " + idProducto);
            }
            return producto;
        } catch (RecursoNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            log.warn("No se pudo obtener producto id {}", idProducto);
            throw new RecursoNoEncontradoException("Producto no encontrado con id: " + idProducto);
        }
    }

    private void validarProductoActivo(ProductoDTO producto) {
        if (!"ACTIVO".equalsIgnoreCase(producto.getEstado())) {
            throw new RuntimeException("No se puede crear inventario para un producto inactivo");
        }
    }

    private void validarStock(Integer stockDisponible, Integer stockMinimo) {
        if (stockDisponible < 0 || stockMinimo < 0) {
            throw new RuntimeException("El stock disponible y mínimo no pueden ser negativos");
        }
        if (stockMinimo > stockDisponible && stockDisponible > 0) {
            log.warn("Inventario quedará bajo stock: disponible {}, mínimo {}", stockDisponible, stockMinimo);
        }
    }

    private String calcularEstado(Integer stockDisponible, Integer stockMinimo) {
        if (stockDisponible == 0) {
            return ESTADO_SIN_STOCK;
        }
        if (stockDisponible <= stockMinimo) {
            return ESTADO_BAJO_STOCK;
        }
        return ESTADO_DISPONIBLE;
    }
}
