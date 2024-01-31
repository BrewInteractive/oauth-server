package com.brew.oauth20.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@SuppressWarnings({"java:S116", "java:S1104"})
@Getter
@Setter
@Builder
public class UserIdentityInfoModel {
    public String sub;
    public String name_surname;
    public OffsetDateTime created_at;
    public OffsetDateTime updated_at;
    public String email;
}
