package com.supplyhub.ms_categorias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.supplyhub.ms_categorias.model.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long>{
}
