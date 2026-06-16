package com.supplyhub.ms_productos.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDTO {
    private Long id;
    private String rutProveedor;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private String estado;
}
