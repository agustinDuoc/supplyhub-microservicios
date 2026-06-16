package com.supplyhub.ms_cotizaciones.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionRequestDTO {

    @NotNull(message = "El producto es obligatorio")
    private Long idProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    // Opcional: el backend no confía en este valor y calcula el total con precio * cantidad.
    private Integer total;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
