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
        validarRutDisponible(dto.getRutEmpresa(), null);
        validarEmailDisponible(dto.getEmail(), null);

        Cliente cliente = new Cliente();
        cliente.setRutEmpresa(dto.getRutEmpresa().trim());
        cliente.setRazonSocial(dto.getRazonSocial().trim());
        cliente.setEmail(dto.getEmail().trim().toLowerCase());
        cliente.setTelefono(dto.getTelefono().trim());
        cliente.setDireccion(dto.getDireccion().trim());
        cliente.setEstado(normalizarEstado(dto.getEstado()));

        return repository.save(cliente);
    }

    public Cliente actualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = buscarPorId(id);
        validarRutDisponible(dto.getRutEmpresa(), id);
        validarEmailDisponible(dto.getEmail(), id);

        cliente.setRutEmpresa(dto.getRutEmpresa().trim());
        cliente.setRazonSocial(dto.getRazonSocial().trim());
        cliente.setEmail(dto.getEmail().trim().toLowerCase());
        cliente.setTelefono(dto.getTelefono().trim());
        cliente.setDireccion(dto.getDireccion().trim());
        cliente.setEstado(normalizarEstado(dto.getEstado()));

        return repository.save(cliente);
    }

    public void eliminar(Long id) {
        Cliente cliente = buscarPorId(id);
        repository.delete(cliente);
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
