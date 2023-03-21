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
@Table(name = "clients_users")
public class ClientsUser {
    @OneToMany(mappedBy = "clientUser")
    private final Set<RefreshToken> refreshTokens = new LinkedHashSet<>();
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @Column(name = "user_id", nullable = false)
    private Long userId;

}