package com.supplyhub.ms_productos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.supplyhub.ms_productos.client.CategoriaClient;
import com.supplyhub.ms_productos.client.ProveedorClient;
import com.supplyhub.ms_productos.dto.CategoriaDTO;
import com.supplyhub.ms_productos.dto.ProductoRequestDTO;
import com.supplyhub.ms_productos.dto.ProductoResponseDTO;
import com.supplyhub.ms_productos.dto.ProveedorDTO;
import com.supplyhub.ms_productos.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_productos.model.Producto;
import com.supplyhub.ms_productos.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {

    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final String ESTADO_INACTIVO = "INACTIVO";

    private final ProductoRepository repository;
    private final CategoriaClient categoriaClient;
    private final ProveedorClient proveedorClient;

    public List<ProductoResponseDTO> listar(String token) {
        return repository.findAll()
                .stream()
                .map(producto -> convertirAResponse(producto, token))
                .toList();
    }

    public ProductoResponseDTO buscarPorId(Long id, String token) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));

        return convertirAResponse(producto, token);
    }

    public ProductoResponseDTO guardar(ProductoRequestDTO dto, String token) {
        validarNombreDisponible(dto.getNombre(), null);
        CategoriaDTO categoria = obtenerCategoriaActiva(dto.getIdCategoria(), token);
        ProveedorDTO proveedor = obtenerProveedorActivo(dto.getIdProveedor(), token);

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre().trim());
        producto.setDescripcion(dto.getDescripcion().trim());
        producto.setPrecio(dto.getPrecio());
        producto.setIdCategoria(categoria.getId());
        producto.setIdProveedor(proveedor.getId());
        producto.setEstado(normalizarEstado(dto.getEstado()));

        Producto guardado = repository.save(producto);
        log.info("Producto creado con id {}", guardado.getId());

        return convertirAResponse(guardado, token);
    }

    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto, String token) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));

        validarNombreDisponible(dto.getNombre(), id);
        CategoriaDTO categoria = obtenerCategoriaActiva(dto.getIdCategoria(), token);
        ProveedorDTO proveedor = obtenerProveedorActivo(dto.getIdProveedor(), token);

        producto.setNombre(dto.getNombre().trim());
        producto.setDescripcion(dto.getDescripcion().trim());
        producto.setPrecio(dto.getPrecio());
        producto.setIdCategoria(categoria.getId());
        producto.setIdProveedor(proveedor.getId());
        producto.setEstado(normalizarEstado(dto.getEstado()));

        return convertirAResponse(repository.save(producto), token);
    }

    public void eliminar(Long id) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));

        repository.delete(producto);
    }

    private ProductoResponseDTO convertirAResponse(Producto producto, String token) {
        CategoriaDTO categoria = obtenerCategoria(producto.getIdCategoria(), token);
        ProveedorDTO proveedor = obtenerProveedor(producto.getIdProveedor(), token);

        return ProductoResponseDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .categoria(categoria)
                .proveedor(proveedor)
                .estado(producto.getEstado())
                .build();
    }

    private void validarNombreDisponible(String nombre, Long idActual) {
        repository.findByNombreIgnoreCase(nombre.trim()).ifPresent(existente -> {
            if (idActual == null || !existente.getId().equals(idActual)) {
                throw new RuntimeException("Ya existe un producto con el nombre: " + nombre);
            }
        });
    }

    private String normalizarEstado(String estado) {
        String limpio = estado.trim().toUpperCase();
        if (!ESTADO_ACTIVO.equals(limpio) && !ESTADO_INACTIVO.equals(limpio)) {
            throw new RuntimeException("El estado del producto debe ser ACTIVO o INACTIVO");
        }
        return limpio;
    }

    private CategoriaDTO obtenerCategoriaActiva(Long idCategoria, String token) {
        CategoriaDTO categoria = obtenerCategoria(idCategoria, token);
        if (!ESTADO_ACTIVO.equalsIgnoreCase(categoria.getEstado())) {
            throw new RuntimeException("No se puede asociar una categoría inactiva al producto");
        }
        return categoria;
    }

    private ProveedorDTO obtenerProveedorActivo(Long idProveedor, String token) {
        ProveedorDTO proveedor = obtenerProveedor(idProveedor, token);
        if (!ESTADO_ACTIVO.equalsIgnoreCase(proveedor.getEstado())) {
            throw new RuntimeException("No se puede asociar un proveedor inactivo al producto");
        }
        return proveedor;
    }

    private CategoriaDTO obtenerCategoria(Long idCategoria, String token) {
        try {
            CategoriaDTO categoria = categoriaClient.obtenerCategoria(idCategoria, token);
            if (categoria == null) {
                throw new RecursoNoEncontradoException("Categoría no encontrada con id: " + idCategoria);
            }
            return categoria;
        } catch (RecursoNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            log.warn("No se pudo obtener categoría id {}", idCategoria);
            throw new RecursoNoEncontradoException("Categoría no encontrada con id: " + idCategoria);
        }
    }

    private ProveedorDTO obtenerProveedor(Long idProveedor, String token) {
        try {
            ProveedorDTO proveedor = proveedorClient.obtenerProveedor(idProveedor, token);
            if (proveedor == null) {
                throw new RecursoNoEncontradoException("Proveedor no encontrado con id: " + idProveedor);
            }
            return proveedor;
        } catch (RecursoNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            log.warn("No se pudo obtener proveedor id {}", idProveedor);
            throw new RecursoNoEncontradoException("Proveedor no encontrado con id: " + idProveedor);
        }
    }
}
