package com.supplyhub.ms_cotizaciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CotizacionResponseDTO {
    private Long id;
    private ProductoDTO producto;
    private Integer cantidad;
    private Integer total;
    private String estado;
}
