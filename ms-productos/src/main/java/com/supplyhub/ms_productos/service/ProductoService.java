package com.supplyhub.ms_productos.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.supplyhub.ms_productos.dto.*;
import com.supplyhub.ms_productos.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_productos.model.Producto;
import com.supplyhub.ms_productos.repository.ProductoRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductoService {

    private final ProductoRepository repository;
    private final WebClient webClient;

    public ProductoService(ProductoRepository repository, WebClient.Builder webClientBuilder) {
        this.repository = repository;
        this.webClient = webClientBuilder.build();
    }

    public List<ProductoResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public ProductoResponseDTO buscarPorId(Long id) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));

        return convertirAResponse(producto);
    }

    public ProductoResponseDTO guardar(ProductoRequestDTO dto) {
        validarCategoria(dto.getIdCategoria());
        validarProveedor(dto.getIdProveedor());

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setIdCategoria(dto.getIdCategoria());
        producto.setIdProveedor(dto.getIdProveedor());
        producto.setEstado(dto.getEstado());

        Producto guardado = repository.save(producto);

        log.info("Producto creado con id {}", guardado.getId());

        return convertirAResponse(guardado);
    }

    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));

        validarCategoria(dto.getIdCategoria());
        validarProveedor(dto.getIdProveedor());

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setIdCategoria(dto.getIdCategoria());
        producto.setIdProveedor(dto.getIdProveedor());
        producto.setEstado(dto.getEstado());

        return convertirAResponse(repository.save(producto));
    }

    public void eliminar(Long id) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));

        repository.delete(producto);
    }

    private ProductoResponseDTO convertirAResponse(Producto producto) {
        CategoriaDTO categoria = obtenerCategoria(producto.getIdCategoria());
        ProveedorDTO proveedor = obtenerProveedor(producto.getIdProveedor());

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

    private void validarCategoria(Long idCategoria) {
        obtenerCategoria(idCategoria);
    }

    private void validarProveedor(Long idProveedor) {
        obtenerProveedor(idProveedor);
    }

    private CategoriaDTO obtenerCategoria(Long idCategoria) {
        try {
            ExternalApiResponse<CategoriaDTO> response = webClient.get()
                    .uri("http://localhost:8084/api/v1/categorias/" + idCategoria)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ExternalApiResponse<CategoriaDTO>>() {})
                    .block();

            return response.getData();

        } catch (Exception e) {
            log.warn("No se pudo obtener categoría id {}", idCategoria);
            throw new RecursoNoEncontradoException("Categoría no encontrada con id: " + idCategoria);
        }
    }

    private ProveedorDTO obtenerProveedor(Long idProveedor) {
        try {
            ExternalApiResponse<ProveedorDTO> response = webClient.get()
                    .uri("http://localhost:8083/api/v1/proveedores/" + idProveedor)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ExternalApiResponse<ProveedorDTO>>() {})
                    .block();

            return response.getData();

        } catch (Exception e) {
            log.warn("No se pudo obtener proveedor id {}", idProveedor);
            throw new RecursoNoEncontradoException("Proveedor no encontrado con id: " + idProveedor);
        }
    }
}