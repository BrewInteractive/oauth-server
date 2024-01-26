package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.ClientUserScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientUserScopeRepository extends JpaRepository<ClientUserScope, UUID> {

}
