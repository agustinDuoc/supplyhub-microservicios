package com.supplyhub.ms_despachos.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DespachoRequestDTO {

    @NotNull(message = "La orden de compra es obligatoria")
    private Long idOrdenCompra;

    @NotNull(message = "El pago es obligatorio")
    private Long idPago;

    @NotBlank(message = "La dirección de envío es obligatoria")
    private String direccionEnvio;

    @NotBlank(message = "El estado del despacho es obligatorio")
    private String estadoDespacho;

    private LocalDate fechaEntrega;
}
