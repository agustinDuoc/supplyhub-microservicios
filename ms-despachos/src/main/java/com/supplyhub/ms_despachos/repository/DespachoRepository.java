package com.supplyhub.ms_despachos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.supplyhub.ms_despachos.model.Despacho;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {
    Optional<Despacho> findByIdOrdenCompra(Long idOrdenCompra);
    Optional<Despacho> findByIdPago(Long idPago);
}
