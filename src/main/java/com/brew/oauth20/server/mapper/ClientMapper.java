package com.brew.oauth20.server.mapper;

import com.brew.oauth20.server.data.*;
import com.brew.oauth20.server.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(source = "clientGrants", target = "grantList", qualifiedByName = "mapClientGrants")
    @Mapping(source = "clientScopes", target = "scopeList", qualifiedByName = "mapClientScopes")
    @Mapping(source = "redirectUris", target = "redirectUriList", qualifiedByName = "mapRedirectUris")
    @Mapping(source = "hooks", target = "hookList", qualifiedByName = "mapHooks")
    ClientModel toDTO(Client client);

    @Named("mapClientGrants")
    default ArrayList<GrantModel> mapClientsGrants(Set<ClientGrant> clientGrants) {
        return clientGrants
                .stream()
                .map(x -> new GrantModel(x.getGrant().getId(), x.getGrant().getResponseType(),
                        x.getGrant().getGrantType()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Named("mapRedirectUris")
    default ArrayList<RedirectUriModel> mapRedirectUris(Set<RedirectUri> redirectUris) {
        return redirectUris
                .stream()
                .map(x -> new RedirectUriModel(x.getId(), x.getRedirectUri()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Named("mapClientScopes")
    default ArrayList<ScopeModel> mapClientsScopes(Set<ClientScope> clientScopes) {
        return clientScopes
                .stream()
                .map(x -> new ScopeModel(x.getId(), x.getScope()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Named("mapHooks")
    default ArrayList<HookModel> mapHooks(Set<Hook> hooks) {
        return hooks
                .stream()
                .map(x -> new HookModel(x.getId(), x.getEndpoint(), x.getHookType(), new ArrayList<>(HookHeaderMapper.INSTANCE.toModelList(x.getHookHeaders()))))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
