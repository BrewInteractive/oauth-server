package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.ActiveAuthorizationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ActiveAuthorizationCodeRepository extends JpaRepository<ActiveAuthorizationCode, UUID> {
    @Query("SELECT v FROM ActiveAuthorizationCode v WHERE v.code = :code AND v.redirectUri = :redirectUri")
    Optional<ActiveAuthorizationCode> findByCodeAndRedirectUri(String code, String redirectUri);
}
