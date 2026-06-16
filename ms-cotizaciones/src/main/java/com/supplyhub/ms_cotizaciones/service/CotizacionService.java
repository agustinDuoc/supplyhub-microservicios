package com.supplyhub.ms_cotizaciones.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.supplyhub.ms_cotizaciones.client.ProductoClient;
import com.supplyhub.ms_cotizaciones.dto.CotizacionRequestDTO;
import com.supplyhub.ms_cotizaciones.dto.CotizacionResponseDTO;
import com.supplyhub.ms_cotizaciones.dto.ProductoDTO;
import com.supplyhub.ms_cotizaciones.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_cotizaciones.model.Cotizacion;
import com.supplyhub.ms_cotizaciones.repository.CotizacionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CotizacionService {

    private static final String ESTADO_VIGENTE = "VIGENTE";
    private static final String ESTADO_ACEPTADA = "ACEPTADA";
    private static final String ESTADO_RECHAZADA = "RECHAZADA";
    private static final String ESTADO_VENCIDA = "VENCIDA";

    private final CotizacionRepository repository;
    private final ProductoClient productoClient;

    public List<CotizacionResponseDTO> listar(String token) {
        return repository.findAll().stream().map(c -> convertir(c, token)).toList();
    }

    public CotizacionResponseDTO buscarPorId(Long id, String token) {
        Cotizacion cotizacion = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cotización no encontrada con id: " + id));
        return convertir(cotizacion, token);
    }

    public CotizacionResponseDTO guardar(CotizacionRequestDTO dto, String token) {
        ProductoDTO producto = obtenerProductoActivo(dto.getIdProducto(), token);

        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setIdProducto(producto.getId());
        cotizacion.setCantidad(dto.getCantidad());
        cotizacion.setTotal(calcularTotal(producto, dto.getCantidad()));
        cotizacion.setEstado(normalizarEstado(dto.getEstado()));

        return convertir(repository.save(cotizacion), token);
    }

    public CotizacionResponseDTO actualizar(Long id, CotizacionRequestDTO dto, String token) {
        Cotizacion cotizacion = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cotización no encontrada con id: " + id));

        ProductoDTO producto = obtenerProductoActivo(dto.getIdProducto(), token);

        cotizacion.setIdProducto(producto.getId());
        cotizacion.setCantidad(dto.getCantidad());
        cotizacion.setTotal(calcularTotal(producto, dto.getCantidad()));
        cotizacion.setEstado(normalizarEstado(dto.getEstado()));

        return convertir(repository.save(cotizacion), token);
    }

    public void eliminar(Long id) {
        Cotizacion cotizacion = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cotización no encontrada con id: " + id));
        repository.delete(cotizacion);
    }

    private CotizacionResponseDTO convertir(Cotizacion cotizacion, String token) {
        ProductoDTO producto = obtenerProducto(cotizacion.getIdProducto(), token);

        return CotizacionResponseDTO.builder()
                .id(cotizacion.getId())
                .producto(producto)
                .cantidad(cotizacion.getCantidad())
                .total(cotizacion.getTotal())
                .estado(cotizacion.getEstado())
                .build();
    }

    private Integer calcularTotal(ProductoDTO producto, Integer cantidad) {
        return producto.getPrecio() * cantidad;
    }

    private String normalizarEstado(String estado) {
        String limpio = estado.trim().toUpperCase();
        if (!ESTADO_VIGENTE.equals(limpio) && !ESTADO_ACEPTADA.equals(limpio)
                && !ESTADO_RECHAZADA.equals(limpio) && !ESTADO_VENCIDA.equals(limpio)) {
            throw new RuntimeException("El estado de la cotización debe ser VIGENTE, ACEPTADA, RECHAZADA o VENCIDA");
        }
        return limpio;
    }

    private ProductoDTO obtenerProductoActivo(Long idProducto, String token) {
        ProductoDTO producto = obtenerProducto(idProducto, token);
        if (!"ACTIVO".equalsIgnoreCase(producto.getEstado())) {
            throw new RuntimeException("No se puede cotizar un producto inactivo");
        }
        return producto;
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
}
