package com.supplyhub.ms_pagos.dto;

import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponseDTO {
    private Long id;
    private OrdenCompraDTO ordenCompra;
    private Integer monto;
    private String metodoPago;
    private String estadoPago;
    private LocalDate fechaPago;
}
