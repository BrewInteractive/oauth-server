package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.ClientsGrant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientsGrantRepository extends JpaRepository<ClientsGrant, UUID> {

}
