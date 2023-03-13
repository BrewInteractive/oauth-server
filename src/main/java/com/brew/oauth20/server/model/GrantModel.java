package com.brew.oauth20.server.model;

import com.brew.oauth20.server.data.enums.ResponseType;
import lombok.Builder;


@Builder
public record GrantModel(Integer id, ResponseType responseType) {
}
