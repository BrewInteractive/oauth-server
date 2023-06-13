package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.AuthorizationCode;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode, UUID> {

    @EntityGraph(attributePaths = "clientUser")
    List<AuthorizationCode> findAll();
}

