package com.brew.oauth20.server.mapper;

import com.brew.oauth20.server.data.ActiveRefreshToken;
import com.brew.oauth20.server.data.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    RefreshTokenMapper INSTANCE = Mappers.getMapper(RefreshTokenMapper.class);

    RefreshToken toRefreshToken(ActiveRefreshToken refreshToken);
}