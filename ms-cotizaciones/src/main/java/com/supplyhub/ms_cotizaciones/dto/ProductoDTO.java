package com.supplyhub.ms_cotizaciones.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer precio;
    private Object categoria;
    private Object proveedor;
    private String estado;
}
