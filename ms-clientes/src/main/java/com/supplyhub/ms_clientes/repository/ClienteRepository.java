package com.supplyhub.ms_clientes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.supplyhub.ms_clientes.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByRutEmpresa(String rutEmpresa);
    Optional<Cliente> findByEmailIgnoreCase(String email);
}
