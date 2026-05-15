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
        validarCategoria(dto.getIdCategoria(), token);
        validarProveedor(dto.getIdProveedor(), token);

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setIdCategoria(dto.getIdCategoria());
        producto.setIdProveedor(dto.getIdProveedor());
        producto.setEstado(dto.getEstado());

        Producto guardado = repository.save(producto);

        log.info("Producto creado con id {}", guardado.getId());

        return convertirAResponse(guardado, token);
    }

    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto, String token) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));

        validarCategoria(dto.getIdCategoria(), token);
        validarProveedor(dto.getIdProveedor(), token);

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setIdCategoria(dto.getIdCategoria());
        producto.setIdProveedor(dto.getIdProveedor());
        producto.setEstado(dto.getEstado());

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

    private void validarCategoria(Long idCategoria, String token) {
        obtenerCategoria(idCategoria, token);
    }

    private void validarProveedor(Long idProveedor, String token) {
        obtenerProveedor(idProveedor, token);
    }

    private CategoriaDTO obtenerCategoria(Long idCategoria, String token) {
        try {
            ExternalApiResponse<CategoriaDTO> response = webClient.get()
                    .uri("http://localhost:8088/api/v1/categorias/" + idCategoria)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ExternalApiResponse<CategoriaDTO>>() {})
                    .block();

            return response != null ? response.getData() : null;

        } catch (Exception e) {
            log.warn("No se pudo obtener categoría id {}", idCategoria);
            throw new RecursoNoEncontradoException("Categoría no encontrada con id: " + idCategoria);
        }
    }

    private ProveedorDTO obtenerProveedor(Long idProveedor, String token) {
        try {
            ExternalApiResponse<ProveedorDTO> response = webClient.get()
                    .uri("http://localhost:8087/api/v1/proveedores/" + idProveedor)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ExternalApiResponse<ProveedorDTO>>() {})
                    .block();

            return response != null ? response.getData() : null;

        } catch (Exception e) {
            log.warn("No se pudo obtener proveedor id {}", idProveedor);
            throw new RecursoNoEncontradoException("Proveedor no encontrado con id: " + idProveedor);
        }
    }
}
