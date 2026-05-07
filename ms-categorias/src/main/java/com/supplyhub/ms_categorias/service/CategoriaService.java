package com.supplyhub.ms_categorias.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.supplyhub.ms_categorias.dto.CategoriaRequestDTO;
import com.supplyhub.ms_categorias.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_categorias.model.Categoria;
import com.supplyhub.ms_categorias.repository.CategoriaRepository;


@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public List<Categoria> listar(){
        return repository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con la id: " + id));
    }

    public Categoria guardar(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setEstado(dto.getEstado());

        return repository.save(categoria);
    }
    
    public Categoria actualizar(Long id, CategoriaRequestDTO dto) {
    Categoria categoria = buscarPorId(id);

    categoria.setNombre(dto.getNombre());
    categoria.setDescripcion(dto.getDescripcion());
    categoria.setEstado(dto.getEstado());

    return repository.save(categoria);
    }

    public void eliminar(Long id) {
    Categoria categoria = buscarPorId(id);
    repository.delete(categoria);
}
}
