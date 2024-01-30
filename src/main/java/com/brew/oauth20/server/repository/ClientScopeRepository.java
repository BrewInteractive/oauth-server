package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.ClientScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientScopeRepository extends JpaRepository<ClientScope, UUID> {

}
