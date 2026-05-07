package com.supplyhub.ms_usuarios.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.supplyhub.ms_usuarios.dto.*;
import com.supplyhub.ms_usuarios.exception.RecursoNoEncontradoException;
import com.supplyhub.ms_usuarios.model.Usuario;
import com.supplyhub.ms_usuarios.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UsuarioService {

    private final UsuarioRepository repository;
    private final WebClient webClient;

    public UsuarioService(UsuarioRepository repository, WebClient.Builder builder) {
        this.repository = repository;
        this.webClient = builder.build();
    }

    public List<UsuarioResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertir)
                .toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + id));

        return convertir(usuario);
    }

    public UsuarioResponseDTO guardar(UsuarioRequestDTO dto) {
        ClienteDTO cliente = obtenerCliente(dto.getIdCliente());

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setRol(dto.getRol());
        usuario.setIdCliente(cliente.getId());
        usuario.setEstado(dto.getEstado());

        Usuario guardado = repository.save(usuario);

        log.info("Usuario creado con id {}", guardado.getId());

        return convertir(guardado);
    }

    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + id));

        ClienteDTO cliente = obtenerCliente(dto.getIdCliente());

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setRol(dto.getRol());
        usuario.setIdCliente(cliente.getId());
        usuario.setEstado(dto.getEstado());

        return convertir(repository.save(usuario));
    }

    public void eliminar(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + id));

        repository.delete(usuario);
    }

    private UsuarioResponseDTO convertir(Usuario usuario) {
        ClienteDTO cliente = obtenerCliente(usuario.getIdCliente());

        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .cliente(cliente)
                .estado(usuario.getEstado())
                .build();
    }

    private ClienteDTO obtenerCliente(Long idCliente) {
        try {
            ExternalApiResponse<ClienteDTO> response = webClient.get()
                    .uri("http://localhost:8081/api/v1/clientes/" + idCliente)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ExternalApiResponse<ClienteDTO>>() {})
                    .block();

            return response.getData();

        } catch (Exception e) {
            log.warn("No se pudo obtener cliente id {}", idCliente);
            throw new RecursoNoEncontradoException("Cliente no encontrado con id: " + idCliente);
        }
    }
}
