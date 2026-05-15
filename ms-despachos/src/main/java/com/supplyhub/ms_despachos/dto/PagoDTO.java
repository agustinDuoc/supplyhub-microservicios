package com.supplyhub.ms_despachos.dto;

import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {
    private Long id;
    private Object ordenCompra;
    private Integer monto;
    private String metodoPago;
    private String estadoPago;
    private LocalDate fechaPago;
}
