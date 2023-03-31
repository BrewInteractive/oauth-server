package com.brew.oauth20.server.data;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grants")
public class Grant {
    @JsonIgnore
    @OneToMany(mappedBy = "grant")
    @ToString.Exclude
    private final Set<ClientsGrant> clientsGrants = new LinkedHashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;
    @Enumerated(EnumType.STRING)
    private ResponseType responseType;
    @Enumerated(EnumType.STRING)
    private GrantType grantType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Grant grant = (Grant) o;
        return getId() != null && Objects.equals(getId(), grant.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}