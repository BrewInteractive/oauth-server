package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.WebOrigin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface WebOriginRepository extends JpaRepository<WebOrigin, UUID> {
    @Query(value = "SELECT wo FROM WebOrigin wo JOIN FETCH wo.client c WHERE c.clientId = :clientId")
    List<WebOrigin> findByClientId(String clientId);
}
