package com.supplyhub.ms_cotizaciones.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.supplyhub.ms_cotizaciones.model.Cotizacion;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
}
