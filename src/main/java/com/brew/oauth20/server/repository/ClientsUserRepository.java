package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.ClientUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ClientsUserRepository extends JpaRepository<ClientUser, UUID> {
    @Query(value = "SELECT distinct cu FROM ClientUser cu JOIN FETCH cu.client c WHERE c.clientId = :clientId AND cu.userId = :userId")
    Optional<ClientUser> findByClientIdAndUserId(String clientId, Long userId);
}