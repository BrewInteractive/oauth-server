package com.brew.oauth20.server.repository;

import com.brew.oauth20.server.data.Hook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HookRepository extends JpaRepository<Hook, UUID> {

}
