package com.harry.counsel.java.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuth2UserDto {
    private final String provider;
    private final String socialId;
    private final String email;
    private final String name;
}
