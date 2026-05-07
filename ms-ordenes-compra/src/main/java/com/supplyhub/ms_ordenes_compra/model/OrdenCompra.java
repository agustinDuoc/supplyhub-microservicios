package com.supplyhub.ms_ordenes_compra.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orden_compra")
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idCliente;
    private Long idInventario;
    private Integer cantidad;
    private Integer total;
    private String estado;
    private LocalDate fechaOrden;
}
