package com.brew.oauth20.server.data;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "clients")
public class Client {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "client_id", nullable = false, length = Integer.MAX_VALUE)
    private String clientId;

    @Column(name = "client_secret", nullable = false, length = Integer.MAX_VALUE)
    private String clientSecret;

    @Column(name = "issue_refresh_tokens", nullable = false)
    private Boolean issueRefreshTokens;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "token_expires_in_minutes", nullable = false)
    private Integer tokenExpiresInMinutes;

    @Column(name = "refresh_token_expires_in_days", nullable = false)
    private Integer refreshTokenExpiresInDays;

    @OneToMany(mappedBy = "client")
    private Set<ClientsGrant> clientsGrants = new LinkedHashSet<>();

    @OneToMany(mappedBy = "client")
    private Set<RedirectUris> redirectUris = new LinkedHashSet<>();

    @OneToMany(mappedBy = "client")
    private Set<ClientsUser> clientsUsers = new LinkedHashSet<>();

}