package com.brew.oauth20.server.data;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "grants")
public class Grant {
    @JsonIgnore
    @OneToMany(mappedBy = "grant")
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

}