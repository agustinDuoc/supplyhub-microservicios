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
        String rut = limpiarTexto(dto.getRutProveedor(), "rutProveedor", 20);
        String nombre = limpiarTexto(dto.getNombre(), "nombre", 150);
        String email = limpiarTexto(dto.getEmail(), "email", 120).toLowerCase();
        String telefono = limpiarTexto(dto.getTelefono(), "telefono", 30);
        String direccion = limpiarTexto(dto.getDireccion(), "direccion", 255);
        validarRutDisponible(rut, null);

        Proveedor proveedor = new Proveedor();
        proveedor.setRutProveedor(rut);
        proveedor.setNombre(nombre);
        proveedor.setEmail(email);
        proveedor.setTelefono(telefono);
        proveedor.setDireccion(direccion);
        proveedor.setEstado(normalizarEstado(dto.getEstado()));

        return repository.save(proveedor);
    }

    public Proveedor actualizar(Long id, ProveedorRequestDTO dto) {
        Proveedor proveedor = buscarPorId(id);
        String rut = limpiarTexto(dto.getRutProveedor(), "rutProveedor", 20);
        String nombre = limpiarTexto(dto.getNombre(), "nombre", 150);
        String email = limpiarTexto(dto.getEmail(), "email", 120).toLowerCase();
        String telefono = limpiarTexto(dto.getTelefono(), "telefono", 30);
        String direccion = limpiarTexto(dto.getDireccion(), "direccion", 255);
        validarRutDisponible(rut, id);

        proveedor.setRutProveedor(rut);
        proveedor.setNombre(nombre);
        proveedor.setEmail(email);
        proveedor.setTelefono(telefono);
        proveedor.setDireccion(direccion);
        proveedor.setEstado(normalizarEstado(dto.getEstado()));

        return repository.save(proveedor);
    }

    public void eliminar(Long id) {
        Proveedor proveedor = buscarPorId(id);
        repository.delete(proveedor);
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
