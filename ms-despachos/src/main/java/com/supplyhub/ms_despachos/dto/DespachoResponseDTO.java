package com.supplyhub.ms_despachos.dto;

import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DespachoResponseDTO {
    private Long id;
    private OrdenCompraDTO ordenCompra;
    private PagoDTO pago;
    private String direccionEnvio;
    private String estadoDespacho;
    private LocalDate fechaEnvio;
    private LocalDate fechaEntrega;
}
