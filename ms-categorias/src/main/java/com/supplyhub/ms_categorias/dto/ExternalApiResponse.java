package com.supplyhub.ms_categorias.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Object error;
}
