package com.supplyhub.ms_pagos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.supplyhub.ms_pagos.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByIdOrdenCompra(Long idOrdenCompra);
}
