package com.brew.oauth20.server.model;

import com.brew.oauth20.server.data.enums.HookType;
import lombok.Builder;

import java.util.ArrayList;
import java.util.UUID;


@Builder
public record HookModel(UUID id, String endpoint, HookType hookType, ArrayList<HookHeaderModel> hookHeaderList) {
}
