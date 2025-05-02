package com.harry.counsel.java.domain.user.dto;

public record OAuth2UserDto(
        String provider,
        String socialId,
        String email,
        String name
) { }
