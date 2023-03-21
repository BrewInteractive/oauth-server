package com.brew.oauth20.server.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "clients")
public class Client {
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    @ToString.Exclude // added "fetch = FetchType.LAZY"
    private final Set<ClientsGrant> clientsGrants = new LinkedHashSet<>();
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    @ToString.Exclude // added "fetch = FetchType.LAZY"
    private final Set<RedirectUris> redirectUris = new LinkedHashSet<>();
    @OneToMany(mappedBy = "client")
    @ToString.Exclude
    private final Set<ClientsUser> clientsUsers = new LinkedHashSet<>();
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
    private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();
    @Column(name = "token_expires_in_minutes", nullable = false)
    private Integer tokenExpiresInMinutes;
    @Column(name = "refresh_token_expires_in_days", nullable = false)
    private Integer refreshTokenExpiresInDays;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Client client = (Client) o;
        return getId() != null && Objects.equals(getId(), client.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}