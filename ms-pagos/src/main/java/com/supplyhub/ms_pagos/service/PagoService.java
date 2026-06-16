package com.supplyhub.ms_pagos.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.supplyhub.ms_pagos.client.OrdenCompraClient;
import com.supplyhub.ms_pagos.dto.OrdenCompraDTO;
import com.supplyhub.ms_pagos.dto.PagoRequestDTO;
import com.supplyhub.ms_pagos.dto.PagoResponseDTO;
import com.supplyhub.ms_pagos.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_pagos.model.Pago;
import com.supplyhub.ms_pagos.repository.PagoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoService {

    private static final String ESTADO_APROBADO = "APROBADO";
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_RECHAZADO = "RECHAZADO";

    private final PagoRepository repository;
    private final OrdenCompraClient ordenCompraClient;

    public List<PagoResponseDTO> listar(String token) {
        return repository.findAll().stream().map(pago -> convertir(pago, token)).toList();
    }

    public PagoResponseDTO buscarPorId(Long id, String token) {
        Pago pago = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado con id: " + id));
        return convertir(pago, token);
    }

    public PagoResponseDTO guardar(PagoRequestDTO dto, String token) {
        OrdenCompraDTO orden = obtenerOrdenCompra(dto.getIdOrdenCompra(), token);
        validarOrdenPagable(orden);
        validarMonto(dto.getMonto(), orden.getTotal());

        repository.findByIdOrdenCompra(orden.getId()).ifPresent(existente -> {
            throw new RuntimeException("Ya existe un pago registrado para la orden de compra id: " + orden.getId());
        });

        Pago pago = new Pago();
        pago.setIdOrdenCompra(orden.getId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(normalizarMetodoPago(dto.getMetodoPago()));
        pago.setEstadoPago(normalizarEstadoPago(dto.getEstadoPago()));
        pago.setFechaPago(LocalDate.now());

        Pago guardado = repository.save(pago);
        log.info("Pago creado con id {}", guardado.getId());

        return convertir(guardado, token);
    }

    public PagoResponseDTO actualizar(Long id, PagoRequestDTO dto, String token) {
        Pago pago = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado con id: " + id));

        OrdenCompraDTO orden = obtenerOrdenCompra(dto.getIdOrdenCompra(), token);
        validarOrdenPagable(orden);
        validarMonto(dto.getMonto(), orden.getTotal());

        repository.findByIdOrdenCompra(orden.getId()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new RuntimeException("Ya existe un pago registrado para la orden de compra id: " + orden.getId());
            }
        });

        pago.setIdOrdenCompra(orden.getId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(normalizarMetodoPago(dto.getMetodoPago()));
        pago.setEstadoPago(normalizarEstadoPago(dto.getEstadoPago()));

        return convertir(repository.save(pago), token);
    }

    public void eliminar(Long id) {
        Pago pago = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado con id: " + id));

        if (ESTADO_APROBADO.equalsIgnoreCase(pago.getEstadoPago())) {
            throw new RuntimeException("No se puede eliminar un pago aprobado");
        }

        repository.delete(pago);
    }

    private PagoResponseDTO convertir(Pago pago, String token) {
        OrdenCompraDTO orden = obtenerOrdenCompra(pago.getIdOrdenCompra(), token);

        return PagoResponseDTO.builder()
                .id(pago.getId())
                .ordenCompra(orden)
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago())
                .estadoPago(pago.getEstadoPago())
                .fechaPago(pago.getFechaPago())
                .build();
    }

    private void validarOrdenPagable(OrdenCompraDTO orden) {
        if ("CANCELADA".equalsIgnoreCase(orden.getEstado())) {
            throw new RuntimeException("No se puede pagar una orden cancelada");
        }
    }

    private void validarMonto(Integer monto, Integer totalOrden) {
        if (monto < totalOrden) {
            throw new RuntimeException("El monto pagado no cubre el total de la orden. Total esperado: " + totalOrden);
        }
    }

    private String normalizarMetodoPago(String metodoPago) {
        String limpio = metodoPago.trim().toUpperCase();
        if (!"TRANSFERENCIA".equals(limpio) && !"TARJETA".equals(limpio) && !"EFECTIVO".equals(limpio)) {
            throw new RuntimeException("El método de pago debe ser TRANSFERENCIA, TARJETA o EFECTIVO");
        }
        return limpio;
    }

    private String normalizarEstadoPago(String estadoPago) {
        String limpio = estadoPago.trim().toUpperCase();
        if (!ESTADO_APROBADO.equals(limpio) && !ESTADO_PENDIENTE.equals(limpio) && !ESTADO_RECHAZADO.equals(limpio)) {
            throw new RuntimeException("El estado del pago debe ser APROBADO, PENDIENTE o RECHAZADO");
        }
        return limpio;
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
}
