package com.sparta.oneandzerobest.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileRequestDto {
    private String name;
    private String introduction;
    private String password;
    private String newPassword;
}
