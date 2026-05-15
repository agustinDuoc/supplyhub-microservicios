package com.supplyhub.ms_despachos.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "despacho")
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idOrdenCompra;
    private Long idPago;
    private String direccionEnvio;
    private String estadoDespacho;
    private LocalDate fechaEnvio;
    private LocalDate fechaEntrega;
}
