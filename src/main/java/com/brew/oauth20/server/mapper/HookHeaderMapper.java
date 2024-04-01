package com.brew.oauth20.server.mapper;

import com.brew.oauth20.server.data.HookHeader;
import com.brew.oauth20.server.model.HookHeaderModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface HookHeaderMapper {
    HookHeaderMapper INSTANCE = Mappers.getMapper(HookHeaderMapper.class);

    HookHeaderModel toModel(HookHeader entity);

    Set<HookHeaderModel> toModelList(Set<HookHeader> entities);
}