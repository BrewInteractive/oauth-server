package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.RedirectUris;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RedirectUrisRepository extends JpaRepository<RedirectUris, UUID> {

}
