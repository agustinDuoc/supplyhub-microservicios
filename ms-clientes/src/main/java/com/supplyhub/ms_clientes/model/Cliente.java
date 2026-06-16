package com.supplyhub.ms_clientes.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rutEmpresa;
    private String razonSocial;
    private String email;
    private String telefono;
    private String direccion;
    private String estado;
}
