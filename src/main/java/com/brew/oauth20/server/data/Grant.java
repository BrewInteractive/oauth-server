package com.brew.oauth20.server.data;

import com.brew.oauth20.server.data.enums.ResponseType;
import com.brew.oauth20.server.data.enums.GrantType;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public GrantType getGrantType() {
        return grantType;
    }

    public void setGrantType(GrantType grantType) {
        this.grantType = grantType;
    }

    public Set<ClientsGrant> getClientsGrants() {
        return clientsGrants;
    }

    public void setClientsGrants(Set<ClientsGrant> clientsGrants) {
        this.clientsGrants = clientsGrants;
    }

}