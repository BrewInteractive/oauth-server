package com.brew.oauth20.server.data;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

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
    private Set<RedirectUris> redirectUrises = new LinkedHashSet<>();

    @OneToMany(mappedBy = "client")
    private Set<ClientsUser> clientsUsers = new LinkedHashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getTokenExpiresInMinutes() {
        return tokenExpiresInMinutes;
    }

    public void setTokenExpiresInMinutes(Integer tokenExpiresInMinutes) {
        this.tokenExpiresInMinutes = tokenExpiresInMinutes;
    }

    public Integer getRefreshTokenExpiresInDays() {
        return refreshTokenExpiresInDays;
    }

    public void setRefreshTokenExpiresInDays(Integer refreshTokenExpiresInDays) {
        this.refreshTokenExpiresInDays = refreshTokenExpiresInDays;
    }

    public Set<ClientsGrant> getClientsGrants() {
        return clientsGrants;
    }

    public void setClientsGrants(Set<ClientsGrant> clientsGrants) {
        this.clientsGrants = clientsGrants;
    }

    public Set<RedirectUris> getRedirectUrises() {
        return redirectUrises;
    }

    public void setRedirectUrises(Set<RedirectUris> redirectUrises) {
        this.redirectUrises = redirectUrises;
    }

    public Set<ClientsUser> getClientsUsers() {
        return clientsUsers;
    }

    public void setClientsUsers(Set<ClientsUser> clientsUsers) {
        this.clientsUsers = clientsUsers;
    }

}