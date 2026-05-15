package com.supplyhub.ms_despachos.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.supplyhub.ms_despachos.model.Despacho;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {
}
