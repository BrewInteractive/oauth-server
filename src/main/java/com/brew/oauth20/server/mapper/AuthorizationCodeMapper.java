package com.brew.oauth20.server.mapper;

import com.brew.oauth20.server.data.ActiveAuthorizationCode;
import com.brew.oauth20.server.data.AuthorizationCode;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AuthorizationCodeMapper {
    AuthorizationCodeMapper INSTANCE = Mappers.getMapper(AuthorizationCodeMapper.class);

    AuthorizationCode toAuthorizationCode(ActiveAuthorizationCode activeAuthorizationCode);
}
