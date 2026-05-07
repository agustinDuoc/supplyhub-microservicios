package com.supplyhub.ms_productos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private Integer precio;

    @NotNull(message = "La categoría es obligatoria")
    private Long idCategoria;

    @NotNull(message = "El proveedor es obligatorio")
    private Long idProveedor;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
