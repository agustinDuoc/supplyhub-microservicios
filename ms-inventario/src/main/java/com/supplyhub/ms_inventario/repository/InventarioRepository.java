package com.supplyhub.ms_inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.supplyhub.ms_inventario.model.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
}
