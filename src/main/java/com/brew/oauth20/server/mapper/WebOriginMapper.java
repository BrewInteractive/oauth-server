package com.brew.oauth20.server.mapper;

import com.brew.oauth20.server.data.WebOrigin;
import com.brew.oauth20.server.model.WebOriginModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WebOriginMapper {
    WebOriginMapper INSTANCE = Mappers.getMapper(WebOriginMapper.class);

    WebOriginModel toModel(WebOrigin entity);

    List<WebOriginModel> toModelList(List<WebOrigin> entities);
}