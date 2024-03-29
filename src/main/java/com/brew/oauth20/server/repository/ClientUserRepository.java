package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.ClientUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientUserRepository extends JpaRepository<ClientUser, UUID> {
    @Query(value = "SELECT distinct cu FROM ClientUser cu " +
            "JOIN FETCH cu.client c " +
            "LEFT JOIN FETCH cu.clientUserScopes cus " +
            "WHERE c.clientId = :clientId " +
            "AND cu.userId = :userId")
    Optional<ClientUser> findByClientIdAndUserId(String clientId, String userId);

    @EntityGraph(attributePaths = "client")
    List<ClientUser> findAll();
}