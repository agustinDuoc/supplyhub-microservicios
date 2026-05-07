package com.supplyhub.ms_inventario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioRequestDTO {

    @NotNull(message = "El producto es obligatorio")
    private Long idProducto;

    @NotNull(message = "Stock disponible obligatorio")
    @Positive
    private Integer stockDisponible;

    @NotNull(message = "Stock mínimo obligatorio")
    @Positive
    private Integer stockMinimo;

    private String ubicacion;

    @NotNull(message = "Estado obligatorio")
    private String estado;
}
