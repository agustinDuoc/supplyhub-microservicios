package com.supplyhub.ms_categorias.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.supplyhub.ms_categorias.dto.CategoriaRequestDTO;
import com.supplyhub.ms_categorias.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_categorias.model.Categoria;
import com.supplyhub.ms_categorias.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final String ESTADO_INACTIVO = "INACTIVO";

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
        String nombre = limpiarTexto(dto.getNombre(), "nombre", 100);
        String descripcion = limpiarTexto(dto.getDescripcion(), "descripcion", 255);
        validarNombreDisponible(nombre, null);

        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoria.setEstado(normalizarEstado(dto.getEstado()));

        return repository.save(categoria);
    }
    
    public Categoria actualizar(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = buscarPorId(id);
        String nombre = limpiarTexto(dto.getNombre(), "nombre", 100);
        String descripcion = limpiarTexto(dto.getDescripcion(), "descripcion", 255);
        validarNombreDisponible(nombre, id);

        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoria.setEstado(normalizarEstado(dto.getEstado()));

        return repository.save(categoria);
    }

    public void eliminar(Long id) {
        Categoria categoria = buscarPorId(id);
        repository.delete(categoria);
    }

    private String limpiarTexto(String valor, String campo, int maximo) {
        if (valor == null) {
            String mensaje = "El campo " + campo + " es obligatorio";
            throw new RuntimeException(mensaje);
        }
        if (valor.isBlank()) {
            String mensaje = "El campo " + campo + " no puede estar vacío";
            throw new RuntimeException(mensaje);
        }
        if (valor.length() > maximo) {
            String mensaje = "El campo " + campo + " no puede superar " + maximo + " caracteres";
            throw new RuntimeException(mensaje);
        }
        return valor.trim();
    }

    private void validarNombreDisponible(String nombre, Long idActual) {
        repository.findByNombreIgnoreCase(nombre.trim()).ifPresent(existente -> {
            if (idActual == null || !existente.getId().equals(idActual)) {
                throw new RuntimeException("Ya existe una categoría con el nombre: " + nombre);
            }
        });
    }

    private String normalizarEstado(String estado) {
        String limpio = estado.trim().toUpperCase();
        if (!ESTADO_ACTIVO.equals(limpio) && !ESTADO_INACTIVO.equals(limpio)) {
            throw new RuntimeException("El estado de la categoría debe ser ACTIVO o INACTIVO");
        }
        return limpio;
    }
}
