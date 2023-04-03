package com.brew.oauth20.server.mapper;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.data.ClientsGrant;
import com.brew.oauth20.server.data.RedirectUris;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.GrantModel;
import com.brew.oauth20.server.model.RedirectUriModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(source = "clientsGrants", target = "grantList", qualifiedByName = "mapClientsGrants")
    @Mapping(source = "redirectUris", target = "redirectUriList", qualifiedByName = "mapRedirectUris")
    ClientModel toDTO(Client client);

    @Named("mapClientsGrants")
    default ArrayList<GrantModel> mapClientsGrants(Set<ClientsGrant> clientsGrants) {
        return clientsGrants
                .stream()
                .map(x -> new GrantModel(x.getGrant().getId(), x.getGrant().getResponseType(), x.getGrant().getGrantType()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Named("mapRedirectUris")
    default ArrayList<RedirectUriModel> mapRedirectUris(Set<RedirectUris> redirectUris) {
        return redirectUris
                .stream()
                .map(x -> new RedirectUriModel(x.getId(), x.getRedirectUri()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
