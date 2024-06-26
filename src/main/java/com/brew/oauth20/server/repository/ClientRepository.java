package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.Client;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    boolean existsByClientId(String clientId);

    @EntityGraph(attributePaths = {"clientGrants.grant", "redirectUris", "clientScopes.scope", "hooks.hookHeaders"})
    Optional<Client> findByClientId(String clientId);

    @EntityGraph(attributePaths = {"clientGrants.grant", "redirectUris", "clientScopes.scope", "hooks.hookHeaders"})
    Optional<Client> findByClientIdAndClientSecret(String clientId, String clientSecret);
}
