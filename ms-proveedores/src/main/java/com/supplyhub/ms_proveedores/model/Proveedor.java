package com.supplyhub.ms_proveedores.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rutProveedor;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private String estado;
}