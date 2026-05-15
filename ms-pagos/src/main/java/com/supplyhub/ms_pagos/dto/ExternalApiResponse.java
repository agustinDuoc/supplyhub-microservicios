package com.supplyhub.ms_pagos.dto;

import lombok.Data;

@Data
public class ExternalApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Object error;
}