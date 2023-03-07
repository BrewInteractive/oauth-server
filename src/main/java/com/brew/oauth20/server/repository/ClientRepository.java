package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
}
