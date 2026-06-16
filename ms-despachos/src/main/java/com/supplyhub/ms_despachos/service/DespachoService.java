package com.supplyhub.ms_despachos.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.supplyhub.ms_despachos.client.OrdenCompraClient;
import com.supplyhub.ms_despachos.client.PagoClient;
import com.supplyhub.ms_despachos.dto.DespachoRequestDTO;
import com.supplyhub.ms_despachos.dto.DespachoResponseDTO;
import com.supplyhub.ms_despachos.dto.OrdenCompraDTO;
import com.supplyhub.ms_despachos.dto.PagoDTO;
import com.supplyhub.ms_despachos.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_despachos.model.Despacho;
import com.supplyhub.ms_despachos.repository.DespachoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DespachoService {

    private static final String ESTADO_PREPARACION = "EN_PREPARACION";
    private static final String ESTADO_ENVIADO = "ENVIADO";
    private static final String ESTADO_ENTREGADO = "ENTREGADO";
    private static final String ESTADO_CANCELADO = "CANCELADO";

    private final DespachoRepository repository;
    private final OrdenCompraClient ordenCompraClient;
    private final PagoClient pagoClient;

    public List<DespachoResponseDTO> listar(String token) {
        return repository.findAll().stream().map(despacho -> convertir(despacho, token)).toList();
    }

    public DespachoResponseDTO buscarPorId(Long id, String token) {
        Despacho despacho = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Despacho no encontrado con id: " + id));
        return convertir(despacho, token);
    }

    public DespachoResponseDTO guardar(DespachoRequestDTO dto, String token) {
        OrdenCompraDTO orden = obtenerOrdenDespachable(dto.getIdOrdenCompra(), token);
        PagoDTO pago = obtenerPagoAprobado(dto.getIdPago(), token);
        validarPagoCorrespondeALaOrden(pago, orden.getId());
        validarFechaEntrega(dto.getFechaEntrega());

        repository.findByIdOrdenCompra(orden.getId()).ifPresent(existente -> {
            throw new RuntimeException("Ya existe un despacho para la orden de compra id: " + orden.getId());
        });
        repository.findByIdPago(pago.getId()).ifPresent(existente -> {
            throw new RuntimeException("Ya existe un despacho asociado al pago id: " + pago.getId());
        });

        Despacho despacho = new Despacho();
        despacho.setIdOrdenCompra(orden.getId());
        despacho.setIdPago(pago.getId());
        despacho.setDireccionEnvio(dto.getDireccionEnvio().trim());
        despacho.setEstadoDespacho(normalizarEstadoDespacho(dto.getEstadoDespacho()));
        despacho.setFechaEnvio(LocalDate.now());
        despacho.setFechaEntrega(dto.getFechaEntrega());

        Despacho guardado = repository.save(despacho);
        log.info("Despacho creado con id {}", guardado.getId());

        return convertir(guardado, token);
    }

    public DespachoResponseDTO actualizar(Long id, DespachoRequestDTO dto, String token) {
        Despacho despacho = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Despacho no encontrado con id: " + id));

        OrdenCompraDTO orden = obtenerOrdenDespachable(dto.getIdOrdenCompra(), token);
        PagoDTO pago = obtenerPagoAprobado(dto.getIdPago(), token);
        validarPagoCorrespondeALaOrden(pago, orden.getId());
        validarFechaEntrega(dto.getFechaEntrega());

        repository.findByIdOrdenCompra(orden.getId()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new RuntimeException("Ya existe un despacho para la orden de compra id: " + orden.getId());
            }
        });
        repository.findByIdPago(pago.getId()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new RuntimeException("Ya existe un despacho asociado al pago id: " + pago.getId());
            }
        });

        despacho.setIdOrdenCompra(orden.getId());
        despacho.setIdPago(pago.getId());
        despacho.setDireccionEnvio(dto.getDireccionEnvio().trim());
        despacho.setEstadoDespacho(normalizarEstadoDespacho(dto.getEstadoDespacho()));
        despacho.setFechaEntrega(dto.getFechaEntrega());

        return convertir(repository.save(despacho), token);
    }

    public void eliminar(Long id) {
        Despacho despacho = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Despacho no encontrado con id: " + id));

        if (ESTADO_ENTREGADO.equalsIgnoreCase(despacho.getEstadoDespacho())) {
            throw new RuntimeException("No se puede eliminar un despacho entregado");
        }

        repository.delete(despacho);
    }

    private DespachoResponseDTO convertir(Despacho despacho, String token) {
        OrdenCompraDTO orden = obtenerOrdenCompra(despacho.getIdOrdenCompra(), token);
        PagoDTO pago = obtenerPago(despacho.getIdPago(), token);

        return DespachoResponseDTO.builder()
                .id(despacho.getId())
                .ordenCompra(orden)
                .pago(pago)
                .direccionEnvio(despacho.getDireccionEnvio())
                .estadoDespacho(despacho.getEstadoDespacho())
                .fechaEnvio(despacho.getFechaEnvio())
                .fechaEntrega(despacho.getFechaEntrega())
                .build();
    }

    private OrdenCompraDTO obtenerOrdenDespachable(Long idOrdenCompra, String token) {
        OrdenCompraDTO orden = obtenerOrdenCompra(idOrdenCompra, token);
        if ("CANCELADA".equalsIgnoreCase(orden.getEstado())) {
            throw new RuntimeException("No se puede despachar una orden cancelada");
        }
        return orden;
    }

    private PagoDTO obtenerPagoAprobado(Long idPago, String token) {
        PagoDTO pago = obtenerPago(idPago, token);
        if (!"APROBADO".equalsIgnoreCase(pago.getEstadoPago())) {
            throw new RuntimeException("No se puede crear despacho porque el pago no está aprobado");
        }
        return pago;
    }

    private void validarPagoCorrespondeALaOrden(PagoDTO pago, Long idOrdenCompra) {
        Long idOrdenDelPago = extraerIdOrdenDesdePago(pago.getOrdenCompra());
        if (!idOrdenCompra.equals(idOrdenDelPago)) {
            throw new RuntimeException("El pago no corresponde a la orden de compra indicada");
        }
    }

    private void validarFechaEntrega(LocalDate fechaEntrega) {
        if (fechaEntrega != null && fechaEntrega.isBefore(LocalDate.now())) {
            throw new RuntimeException("La fecha de entrega no puede ser anterior a la fecha de envío");
        }
    }

    private String normalizarEstadoDespacho(String estadoDespacho) {
        String limpio = estadoDespacho.trim().toUpperCase();
        if (!ESTADO_PREPARACION.equals(limpio) && !ESTADO_ENVIADO.equals(limpio)
                && !ESTADO_ENTREGADO.equals(limpio) && !ESTADO_CANCELADO.equals(limpio)) {
            throw new RuntimeException("El estado del despacho debe ser EN_PREPARACION, ENVIADO, ENTREGADO o CANCELADO");
        }
        return limpio;
    }

    private Long extraerIdOrdenDesdePago(Object ordenCompra) {
        if (ordenCompra instanceof Map<?, ?> map) {
            Object id = map.get("id");
            if (id instanceof Long valor) {
                return valor;
            }
            if (id instanceof Integer valor) {
                return valor.longValue();
            }
            if (id instanceof Number numero) {
                return numero.longValue();
            }
        }
        throw new RuntimeException("No se pudo validar la orden asociada al pago");
    }

    private OrdenCompraDTO obtenerOrdenCompra(Long idOrdenCompra, String token) {
        try {
            OrdenCompraDTO orden = ordenCompraClient.obtenerOrdenCompra(idOrdenCompra, token);
            if (orden == null) {
                throw new RecursoNoEncontradoException("Orden de compra no encontrada con id: " + idOrdenCompra);
            }
            return orden;
        } catch (RecursoNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            log.warn("No se pudo obtener orden de compra id {}", idOrdenCompra);
            throw new RecursoNoEncontradoException("Orden de compra no encontrada con id: " + idOrdenCompra);
        }
    }

    private PagoDTO obtenerPago(Long idPago, String token) {
        try {
            PagoDTO pago = pagoClient.obtenerPago(idPago, token);
            if (pago == null) {
                throw new RecursoNoEncontradoException("Pago no encontrado con id: " + idPago);
            }
            return pago;
        } catch (RecursoNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            log.warn("No se pudo obtener pago id {}", idPago);
            throw new RecursoNoEncontradoException("Pago no encontrado con id: " + idPago);
        }
    }
}
