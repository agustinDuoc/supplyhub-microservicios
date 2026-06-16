package com.supplyhub.ms_categorias.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.supplyhub.ms_categorias.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>{
    Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
