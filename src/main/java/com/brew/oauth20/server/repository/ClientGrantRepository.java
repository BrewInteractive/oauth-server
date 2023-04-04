package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.ClientGrant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientGrantRepository extends JpaRepository<ClientGrant, UUID> {

}
