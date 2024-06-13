package com.sparta.oneandzerobest.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileResponseDto {
    private String username;
    private String name;
    private String introduction;
    private String email;
}
