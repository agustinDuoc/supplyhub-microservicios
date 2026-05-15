package com.supplyhub.ms_ordenes_compra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraRequestDTO {

    @NotNull(message = "El cliente es obligatorio")
    private Long idCliente;

    @NotNull(message = "El inventario es obligatorio")
    private Long idInventario;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    @NotNull(message = "El total es obligatorio")
    @Positive(message = "El total debe ser mayor a cero")
    private Integer total;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
