package com.supplyhub.ms_ordenes_compra.dto;

import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompraResponseDTO {
    private Long id;
    private ClienteDTO cliente;
    private InventarioDTO inventario;
    private Integer cantidad;
    private Integer total;
    private String estado;
    private LocalDate fechaOrden;
}
