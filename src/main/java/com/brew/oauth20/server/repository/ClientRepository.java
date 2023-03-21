package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.model.ClientModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    default Optional<ClientModel> findByIdWithDetails(UUID id) {
        Optional<Client> optionalClient = findById(id);
        return optionalClient.map(ClientMapper.INSTANCE::toDTO);
    }
}

