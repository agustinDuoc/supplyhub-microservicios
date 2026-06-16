package com.supplyhub.ms_pagos.dto;

import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraDTO {
    private Long id;
    private Object cliente;
    private Object inventario;
    private Integer cantidad;
    private Integer total;
    private String estado;
    private LocalDate fechaOrden;
}
