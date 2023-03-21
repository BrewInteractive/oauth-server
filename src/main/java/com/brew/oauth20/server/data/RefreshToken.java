package com.brew.oauth20.server.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
@Table(name = "refresh_tokens")
public class RefreshToken {
    @OneToMany(mappedBy = "replacedByToken")
    @ToString.Exclude
    private final Set<RefreshToken> refreshTokens = new LinkedHashSet<>();
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "created_at", nullable = false)
    @Generated(value = GenerationTime.INSERT)
    private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    @Generated(value = GenerationTime.ALWAYS)
    private OffsetDateTime updatedAt;
    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;
    @Column(name = "token", nullable = false, length = Integer.MAX_VALUE)
    private String token;
    @Column(name = "revoked_at", nullable = false)
    private OffsetDateTime revokedAt;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "replaced_by_token_id", nullable = false)
    @ToString.Exclude
    private RefreshToken replacedByToken;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "client_user_id", nullable = false)
    @ToString.Exclude
    private ClientsUser clientUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RefreshToken that = (RefreshToken) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}