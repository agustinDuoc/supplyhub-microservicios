package com.supplyhub.ms_productos.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer precio;
    private CategoriaDTO categoria;
    private ProveedorDTO proveedor;
    private String estado;
}

