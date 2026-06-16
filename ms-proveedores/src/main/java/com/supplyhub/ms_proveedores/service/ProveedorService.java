package com.supplyhub.ms_proveedores.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.supplyhub.ms_proveedores.dto.ProveedorRequestDTO;
import com.supplyhub.ms_proveedores.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_proveedores.model.Proveedor;
import com.supplyhub.ms_proveedores.repository.ProveedorRepository;

@Service
public class ProveedorService {

    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final String ESTADO_INACTIVO = "INACTIVO";

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
        validarRutDisponible(dto.getRutProveedor(), null);

        Proveedor proveedor = new Proveedor();
        proveedor.setRutProveedor(dto.getRutProveedor().trim());
        proveedor.setNombre(dto.getNombre().trim());
        proveedor.setEmail(dto.getEmail().trim().toLowerCase());
        proveedor.setTelefono(dto.getTelefono().trim());
        proveedor.setDireccion(dto.getDireccion().trim());
        proveedor.setEstado(normalizarEstado(dto.getEstado()));

        return repository.save(proveedor);
    }

    public Proveedor actualizar(Long id, ProveedorRequestDTO dto) {
        Proveedor proveedor = buscarPorId(id);
        validarRutDisponible(dto.getRutProveedor(), id);

        proveedor.setRutProveedor(dto.getRutProveedor().trim());
        proveedor.setNombre(dto.getNombre().trim());
        proveedor.setEmail(dto.getEmail().trim().toLowerCase());
        proveedor.setTelefono(dto.getTelefono().trim());
        proveedor.setDireccion(dto.getDireccion().trim());
        proveedor.setEstado(normalizarEstado(dto.getEstado()));

        return repository.save(proveedor);
    }

    public void eliminar(Long id) {
        Proveedor proveedor = buscarPorId(id);
        repository.delete(proveedor);
    }

    private void validarRutDisponible(String rutProveedor, Long idActual) {
        repository.findByRutProveedor(rutProveedor.trim()).ifPresent(existente -> {
            if (idActual == null || !existente.getId().equals(idActual)) {
                throw new RuntimeException("Ya existe un proveedor con el RUT: " + rutProveedor);
            }
        });
    }

    private String normalizarEstado(String estado) {
        String limpio = estado.trim().toUpperCase();
        if (!ESTADO_ACTIVO.equals(limpio) && !ESTADO_INACTIVO.equals(limpio)) {
            throw new RuntimeException("El estado del proveedor debe ser ACTIVO o INACTIVO");
        }
        return limpio;
    }
}
