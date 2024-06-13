package com.sparta.oneandzerobest.auth.entity;

import com.sparta.oneandzerobest.profile.dto.ProfileRequestDto;
import com.sparta.oneandzerobest.s3.entity.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class UserEntityTest {

    User user;
    String username;
    String password;
    String name;
    String email;
    UserStatus statusCode;

    @BeforeEach
    void setUp() {
        username = "testUsername";
        password = "testPassword";
        name = "testName";
        email = "testEmail";
        statusCode = UserStatus.UNVERIFIED;

        user = new User(username, password, name, email, statusCode);
    }

    @Test
    @DisplayName("User(): 생성자 테스트")
    void test1() {
        // When - Then
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    @DisplayName("onCreate() 테스트")
    void test2() {
        // When
        user.onCreate();

        // Then
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("onUpdate() 테스트")
    void test3() {
        // When
        user.onUpdate();

        // Then
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("getAuthorities() 테스트")
    void test4() {
        // Then
        assertNotNull(user.getAuthorities());
    }

    @Test
    @DisplayName("setProfileImage() 테스트")
    void test5() {
        // Given
        Image image = Mockito.mock(Image.class);

        // When
        user.setProfileImage(image);

        // Then
        assertEquals(image, user.getImage());
    }

    @Test
    @DisplayName("update() 테스트")
    void test6() {
        // Given
        String updateName = "updateName";
        String updateIntroduction = "updateIntroduction";

        ProfileRequestDto requestDto = new ProfileRequestDto(updateName, updateIntroduction, "password", "newPassword");

        // When
        user.update(requestDto);

        // Then
        assertEquals(updateName, user.getName());
        assertEquals(updateIntroduction, user.getIntroduction());
    }

    @Test
    @DisplayName("updateRefreshToken() 테스트")
    void test7() {
        // Given
        String updateRefreshToken = "update_refresh_token";

        // WHen
        user.updateRefreshToken(updateRefreshToken);

        // Then
        assertEquals(updateRefreshToken, user.getRefreshToken());
    }

    @Test
    @DisplayName("withdraw() 테스트")
    void test8() {
        // When
        user.withdraw();

        // Then
        assertEquals(UserStatus.WITHDRAWN, user.getStatusCode());
        assertNull(user.getRefreshToken());
    }

    @Test
    @DisplayName("updateEmail() 테스트")
    void test9() {
        // Given
        String updateEmail = "updateEmail@google.com";

        // When
        user.updateEmail(updateEmail);

        // Then
        assertEquals(updateEmail, user.getEmail());
    }

    @Test
    @DisplayName("updateStatus() 테스트")
    void test10() {
        // Given
        UserStatus updateUserStatus = UserStatus.ACTIVE;

        // When
        user.updateStatus(updateUserStatus);

        // Then
        assertEquals(updateUserStatus, user.getStatusCode());
    }

    @Test
    @DisplayName("updateKakaoUser() 테스트")
    void test11() {
        // Given
        long testId = 77L;
        String testUsername = "socialUser";
        String testNickName = "socialNickName";
        String testEmail = "social@test.com";
        UserStatus testStatus = UserStatus.ACTIVE;

        // When
        user.updateKakaoUser(testId, testUsername, testNickName, testEmail, testStatus);

        // Then
        assertEquals(testId, user.getId());
        assertEquals(testUsername, user.getUsername());
        assertEquals("kakao", user.getPassword());
        assertEquals(testEmail, user.getEmail());
        assertEquals(testNickName, user.getName());
        assertEquals(UserStatus.ACTIVE, user.getStatusCode());
    }

    @Test
    @DisplayName("updateGoogleUser() 테스트")
    void test12() {
        // Given
        long testId = 77L;
        String testUsername = "socialUser";
        String testNickName = "socialNickName";
        String testEmail = "social@test.com";
        UserStatus testStatus = UserStatus.ACTIVE;

        // When
        user.updateGoogleUser(testId, testUsername, testNickName, testEmail, testStatus);

        // Then
        assertEquals(testId, user.getId());
        assertEquals(testUsername, user.getUsername());
        assertEquals("google", user.getPassword());
        assertEquals(testEmail, user.getEmail());
        assertEquals(testNickName, user.getName());
        assertEquals(UserStatus.ACTIVE, user.getStatusCode());
    }

    @Test
    @DisplayName("updateGithubUser() 테스트")
    void test13() {
        // Given
        long testId = 77L;
        String testUsername = "socialUser";
        String testNickName = "socialNickName";
        String testEmail = "social@test.com";
        UserStatus testStatus = UserStatus.ACTIVE;

        // When
        user.updateGithubUser(testId, testUsername, testNickName, testEmail, testStatus);

        // Then
        assertEquals(testId, user.getId());
        assertEquals(testUsername, user.getUsername());
        assertEquals("github", user.getPassword());
        assertEquals(testEmail, user.getEmail());
        assertEquals(testNickName, user.getName());
        assertEquals(UserStatus.ACTIVE, user.getStatusCode());
    }

    @Test
    @DisplayName("clearRefreshToken() 테스트")
    void test14() {
        // when
        user.clearRefreshToken();

        // Then
        assertNull(user.getRefreshToken());
    }

    @Test
    @DisplayName("updatePassword() 테스트")
    void test15() {
        // Given
        String updatePassword = "updatePassword";

        // When
        user.updatePassword(updatePassword);

        // Then
        assertEquals(updatePassword, user.getPassword());
    }
}
