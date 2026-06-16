package com.supplyhub.ms_inventario.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioResponseDTO {

    private Long id;
    private ProductoDTO producto;
    private Integer stockDisponible;
    private Integer stockMinimo;
    private String ubicacion;
    private String estado;
}
