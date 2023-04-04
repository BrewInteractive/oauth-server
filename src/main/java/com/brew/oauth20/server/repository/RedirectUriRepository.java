package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.RedirectUri;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RedirectUriRepository extends JpaRepository<RedirectUri, UUID> {

}
