package com.brew.oauth20.server.data;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients")
public class Client {
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    @ToString.Exclude // added "fetch = FetchType.LAZY"
    private final Set<ClientGrant> clientGrants = new LinkedHashSet<>();
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    @ToString.Exclude // added "fetch = FetchType.LAZY"
    private final Set<ClientScope> clientScopes = new LinkedHashSet<>();
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    @ToString.Exclude // added "fetch = FetchType.LAZY"
    private final Set<RedirectUri> redirectUris = new LinkedHashSet<>();
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    @ToString.Exclude // added "fetch = FetchType.LAZY"
    private final Set<Hook> hooks = new LinkedHashSet<>();
    @OneToMany(mappedBy = "client")
    @ToString.Exclude
    private final Set<ClientUser> clientUsers = new LinkedHashSet<>();
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;
    @Column(name = "client_id", nullable = false, length = Integer.MAX_VALUE)
    private String clientId;
    @Column(name = "client_secret", nullable = false, length = Integer.MAX_VALUE)
    private String clientSecret;
    @Column(name = "audience", length = Integer.MAX_VALUE)
    private String audience;
    @Column(name = "issuer_uri", length = Integer.MAX_VALUE)
    private String issuerUri;
    @Column(name = "issue_refresh_tokens", nullable = false)
    private Boolean issueRefreshTokens;
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
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Client client = (Client) o;
        return getId() != null && Objects.equals(getId(), client.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}