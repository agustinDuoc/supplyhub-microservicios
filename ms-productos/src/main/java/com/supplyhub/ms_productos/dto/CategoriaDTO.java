package com.supplyhub.ms_productos.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String estado;
}
