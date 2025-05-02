package com.harry.counsel.java.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
    private String provider;
    private String role;
}
