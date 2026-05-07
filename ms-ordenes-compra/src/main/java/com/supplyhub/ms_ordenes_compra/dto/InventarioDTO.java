package com.supplyhub.ms_ordenes_compra.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioDTO {
    private Long id;
    private Object producto;
    private Integer stockDisponible;
    private Integer stockMinimo;
    private String ubicacion;
    private String estado;
}
