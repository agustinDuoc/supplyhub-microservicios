package com.supplyhub.ms_auth.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.supplyhub.ms_auth.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}
