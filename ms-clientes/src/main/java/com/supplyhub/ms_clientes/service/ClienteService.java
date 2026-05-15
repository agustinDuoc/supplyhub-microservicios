package com.supplyhub.ms_clientes.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.supplyhub.ms_clientes.dto.ClienteRequestDTO;
import com.supplyhub.ms_clientes.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_clientes.model.Cliente;
import com.supplyhub.ms_clientes.repository.ClienteRepository;

@Service
public class ClienteService {

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
        Cliente cliente = new Cliente();
        cliente.setRutEmpresa(dto.getRutEmpresa());
        cliente.setRazonSocial(dto.getRazonSocial());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setEstado(dto.getEstado());

        return repository.save(cliente);
    }

    public Cliente actualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = buscarPorId(id);

        cliente.setRutEmpresa(dto.getRutEmpresa());
        cliente.setRazonSocial(dto.getRazonSocial());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setEstado(dto.getEstado());

        return repository.save(cliente);
    }

    public void eliminar(Long id) {
        Cliente cliente = buscarPorId(id);
        repository.delete(cliente);
    }
}