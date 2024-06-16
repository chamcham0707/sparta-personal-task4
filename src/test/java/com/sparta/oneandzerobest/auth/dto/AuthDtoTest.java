package com.sparta.oneandzerobest.auth.dto;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.sparta.oneandzerobest.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class AuthDtoTest {

    FixtureMonkey fixtureMonkey;

    @BeforeEach
    void setUp() {
        fixtureMonkey = FixtureMonkey.builder()
                .defaultNotNull(Boolean.TRUE)
                .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
                .build();
    }

    @Test
    @DisplayName("RefreshTokenDTO 테스트")
    void test1() {
        // Given
        String testToken = "test_refresh_token";
        RefreshTokenRequestDto refreshTokenRequestDto = fixtureMonkey.giveMeBuilder(RefreshTokenRequestDto.class)
                .set("refreshToken", testToken)
                .sample();

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
        String testEmail = "test@example.com";

        SignupRequestDto signupRequestDto = fixtureMonkey.giveMeBuilder(SignupRequestDto.class)
                .set("username", testUsername)
                .set("password", testPassword)
                .set("name", testName)
                .set("email", testEmail)
                .sample();

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

        TokenResponseDto tokenResponseDto = fixtureMonkey.giveMeBuilder(TokenResponseDto.class)
                .set("accessToken", testAccessToken)
                .set("refreshToken", testRefreshToken)
                .sample();

        // When - Then
        assertEquals(tokenResponseDto.getAccessToken(), testAccessToken);
        assertEquals(tokenResponseDto.getRefreshToken(), testRefreshToken);
    }
}
