package com.brew.oauth20.server.data;

import com.brew.oauth20.server.data.enums.GrantType;
import com.brew.oauth20.server.data.enums.ResponseType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "grants")
public class Grant {
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

    @OneToMany(mappedBy = "grant")
    private Set<ClientsGrant> clientsGrants = new LinkedHashSet<>();

}