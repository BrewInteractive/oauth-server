package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.ActiveRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ActiveRefreshTokenRepository extends JpaRepository<ActiveRefreshToken, UUID> {
    @Query("SELECT v FROM ActiveRefreshToken v WHERE v.token = :token")
    Optional<ActiveRefreshToken> findByToken(String token);
}