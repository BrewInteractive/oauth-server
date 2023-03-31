package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.Grant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GrantRepository extends JpaRepository<Grant, UUID> {

}
