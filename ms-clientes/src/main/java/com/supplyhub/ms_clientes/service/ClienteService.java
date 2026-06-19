package com.supplyhub.ms_clientes.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.supplyhub.ms_clientes.dto.ClienteRequestDTO;
import com.supplyhub.ms_clientes.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_clientes.model.Cliente;
import com.supplyhub.ms_clientes.repository.ClienteRepository;

@Service
public class ClienteService {

    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final String ESTADO_INACTIVO = "INACTIVO";

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public List<Cliente> listar() {
        return repository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));
    }

    public Cliente guardar(ClienteRequestDTO dto) {
        String rut = limpiarTexto(dto.getRutEmpresa(), "rutEmpresa", 20);
        String razonSocial = limpiarTexto(dto.getRazonSocial(), "razonSocial", 150);
        String email = limpiarTexto(dto.getEmail(), "email", 120).toLowerCase();
        String telefono = limpiarTexto(dto.getTelefono(), "telefono", 30);
        String direccion = limpiarTexto(dto.getDireccion(), "direccion", 255);
        validarRutDisponible(rut, null);
        validarEmailDisponible(email, null);

        Cliente cliente = new Cliente();
        cliente.setRutEmpresa(rut);
        cliente.setRazonSocial(razonSocial);
        cliente.setEmail(email);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);
        cliente.setEstado(normalizarEstado(dto.getEstado()));

        return repository.save(cliente);
    }

    public Cliente actualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = buscarPorId(id);
        String rut = limpiarTexto(dto.getRutEmpresa(), "rutEmpresa", 20);
        String razonSocial = limpiarTexto(dto.getRazonSocial(), "razonSocial", 150);
        String email = limpiarTexto(dto.getEmail(), "email", 120).toLowerCase();
        String telefono = limpiarTexto(dto.getTelefono(), "telefono", 30);
        String direccion = limpiarTexto(dto.getDireccion(), "direccion", 255);
        validarRutDisponible(rut, id);
        validarEmailDisponible(email, id);

        cliente.setRutEmpresa(rut);
        cliente.setRazonSocial(razonSocial);
        cliente.setEmail(email);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);
        cliente.setEstado(normalizarEstado(dto.getEstado()));

        return repository.save(cliente);
    }

    public void eliminar(Long id) {
        Cliente cliente = buscarPorId(id);
        repository.delete(cliente);
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

    private void validarRutDisponible(String rutEmpresa, Long idActual) {
        repository.findByRutEmpresa(rutEmpresa.trim()).ifPresent(existente -> {
            if (idActual == null || !existente.getId().equals(idActual)) {
                throw new RuntimeException("Ya existe un cliente con el RUT: " + rutEmpresa);
            }
        });
    }

    private void validarEmailDisponible(String email, Long idActual) {
        repository.findByEmailIgnoreCase(email.trim()).ifPresent(existente -> {
            if (idActual == null || !existente.getId().equals(idActual)) {
                throw new RuntimeException("Ya existe un cliente con el email: " + email);
            }
        });
    }

    private String normalizarEstado(String estado) {
        String limpio = estado.trim().toUpperCase();
        if (!ESTADO_ACTIVO.equals(limpio) && !ESTADO_INACTIVO.equals(limpio)) {
            throw new RuntimeException("El estado del cliente debe ser ACTIVO o INACTIVO");
        }
        return limpio;
    }
}
