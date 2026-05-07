package com.supplyhub.ms_proveedores.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.supplyhub.ms_proveedores.dto.ProveedorRequestDTO;
import com.supplyhub.ms_proveedores.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_proveedores.model.Proveedor;
import com.supplyhub.ms_proveedores.repository.ProveedorRepository;

@Service
public class ProveedorService {

    private final ProveedorRepository repository;

    public ProveedorService(ProveedorRepository repository) {
        this.repository = repository;
    }

    public List<Proveedor> listar() {
        return repository.findAll();
    }

    public Proveedor buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id: " + id));
    }

    public Proveedor guardar(ProveedorRequestDTO dto) {
        if (repository.findByRutProveedor(dto.getRutProveedor()).isPresent()) {
            throw new RuntimeException("El RUT del proveedor ya existe");
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setRutProveedor(dto.getRutProveedor());
        proveedor.setNombre(dto.getNombre());
        proveedor.setEmail(dto.getEmail());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setEstado(dto.getEstado());

        return repository.save(proveedor);
    }

    public Proveedor actualizar(Long id, ProveedorRequestDTO dto) {
        Proveedor proveedor = buscarPorId(id);

        proveedor.setRutProveedor(dto.getRutProveedor());
        proveedor.setNombre(dto.getNombre());
        proveedor.setEmail(dto.getEmail());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setEstado(dto.getEstado());

        return repository.save(proveedor);
    }

    public void eliminar(Long id) {
        Proveedor proveedor = buscarPorId(id);
        repository.delete(proveedor);
    }
}
