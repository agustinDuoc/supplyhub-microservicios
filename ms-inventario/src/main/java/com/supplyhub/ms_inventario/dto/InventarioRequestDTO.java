package com.supplyhub.ms_inventario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioRequestDTO {

    @NotNull(message = "El producto es obligatorio")
    private Long idProducto;

    @NotNull(message = "Stock disponible obligatorio")
    @PositiveOrZero(message = "El stock disponible no puede ser negativo")
    private Integer stockDisponible;

    @NotNull(message = "Stock mínimo obligatorio")
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    private String ubicacion;

    @NotNull(message = "Estado obligatorio")
    private String estado;
}
