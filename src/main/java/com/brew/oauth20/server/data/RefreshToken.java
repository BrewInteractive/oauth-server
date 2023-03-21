package com.brew.oauth20.server.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @OneToMany(mappedBy = "replacedByToken")
    private final Set<RefreshToken> refreshTokens = new LinkedHashSet<>();
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;
    @Column(name = "token", nullable = false, length = Integer.MAX_VALUE)
    private String token;
    @Column(name = "revoked_at", nullable = false)
    private OffsetDateTime revokedAt;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "replaced_by_token_id", nullable = false)
    private RefreshToken replacedByToken;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "client_user_id", nullable = false)
    private ClientsUser clientUser;

}