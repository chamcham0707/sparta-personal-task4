package com.sparta.oneandzerobest.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class AuthDtoTest {
    @Test
    @DisplayName("RefreshTokenDTO 테스트")
    void test1() {
        // Given
        String testToken = "test_refresh_token";
        RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto();

        try {
            Field field = RefreshTokenRequestDto.class.getDeclaredField("refreshToken");
            field.setAccessible(true);
            field.set(refreshTokenRequestDto, testToken);
        } catch (Exception e) {
            fail("test fail - error message: " + e.getMessage());
        }

        // When
        String refreshToken = refreshTokenRequestDto.getRefreshToken();

        // Then
        assertEquals(testToken, refreshToken);
    }

    @Test
    @DisplayName("SignupReqquestDto 테스트")
    void test2() {
        // Given
        String testUsername = "username1";
        String testPassword = "password1";
        String testName = "name1";
        String testEmail = "email1";

        SignupRequestDto signupRequestDto = new SignupRequestDto(testUsername, testPassword, testName, testEmail);

        // When - Then
        assertEquals(signupRequestDto.getUsername(), testUsername);
        assertEquals(signupRequestDto.getPassword(), testPassword);
        assertEquals(signupRequestDto.getName(), testName);
        assertEquals(signupRequestDto.getEmail(), testEmail);
    }

    @Test
    @DisplayName("TokenResponseDto 테스트")
    void test3() {
        // Given
        String testAccessToken = "test_access_token";
        String testRefreshToken = "test_refresh_token";

        TokenResponseDto tokenResponseDto = new TokenResponseDto(testAccessToken, testRefreshToken);

        // When - Then
        assertEquals(tokenResponseDto.getAccessToken(), testAccessToken);
        assertEquals(tokenResponseDto.getRefreshToken(), testRefreshToken);
    }
}
