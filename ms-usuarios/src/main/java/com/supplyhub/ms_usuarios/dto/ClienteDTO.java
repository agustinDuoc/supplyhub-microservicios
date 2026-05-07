package com.supplyhub.ms_usuarios.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Long id;
    private String rutEmpresa;
    private String razonSocial;
    private String email;
    private String telefono;
    private String direccion;
    private String estado;
}