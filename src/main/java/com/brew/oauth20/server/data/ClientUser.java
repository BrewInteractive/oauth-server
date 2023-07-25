package com.brew.oauth20.server.data;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
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
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients_users")
public class ClientUser {
    @OneToMany(mappedBy = "clientUser")
    @ToString.Exclude
    private final Set<RefreshToken> refreshTokens = new LinkedHashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "client_id", nullable = false)
    @ToString.Exclude
    private Client client;
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ClientUser that = (ClientUser) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}