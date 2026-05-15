package com.supplyhub.ms_pagos.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idOrdenCompra;
    private Integer monto;
    private String metodoPago;
    private String estadoPago;
    private LocalDate fechaPago;
}