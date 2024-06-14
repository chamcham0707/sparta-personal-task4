package com.sparta.oneandzerobest.auth.service;

import com.sparta.oneandzerobest.auth.entity.*;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceIntegrationTest {

    final static String username = "testUser123";
    final static String email = "test@example.com";
    final static String password = "Password!123";
    final static String encodePassword = "testEncodePassword";
    final static String accessToken = "testAccessToken";
    final static String refreshToken = "testRefreshToken";

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    User user;

    @BeforeEach
    void setUp() {
        user = new User(username, encodePassword, "testName", email, UserStatus.UNVERIFIED);
    }

    @Test
    @Order(1)
    @DisplayName("회원가입 테스트")
    void test1() {
        // Given
        SignupRequest signupRequest = new SignupRequest(username, password, email, false, "");

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When - Then
        assertDoesNotThrow(() -> userService.signup(signupRequest));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @Order(2)
    @DisplayName("이메일 인증 테스트")
    void test2() {
        // Given
        String verificationCode = redisTemplate.opsForValue().get(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        boolean result = userService.verifyEmail(username, verificationCode);

        // Then
        assertTrue(result);
    }


    @Test
    @Order(3)
    @DisplayName("로그인 테스트")
    void test3() {
        // Given
        LoginRequest loginRequest = new LoginRequest(username, password);

        user.updateStatus(UserStatus.ACTIVE);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), encodePassword)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        LoginResponse loginResponse = userService.login(loginRequest);

        // Then
        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getAccessToken());
        assertNotNull(loginResponse.getRefreshToken());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @Order(4)
    @DisplayName("로그아웃 테스트")
    void test4() {
        // Given
        user.updateStatus(UserStatus.ACTIVE);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.logout(username, accessToken, refreshToken);

        // Then
        assertNull(user.getRefreshToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void test5() {
        // Given
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodePassword)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.withdraw(username, password, accessToken, refreshToken);

        // Then
        assertEquals(UserStatus.WITHDRAWN, user.getStatusCode());
        assertNull(user.getRefreshToken());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("OAuth 로그인 테스트")
    void test6() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        LoginResponse loginResponse = userService.loginWithOAuth(email);

        // Then
        assertNotNull(loginResponse);
        assertNotEquals(refreshToken, user.getRefreshToken());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("카카오 로그인 테스트")
    void test7() {
        // Given
        String userInfoJson = "{ \"id\": 12345, \"properties\": { \"nickname\": \"testUser\" } }";
        long kakaoId = 12345L;
        String nickname = "testUser";
        String email = kakaoId + "aA@naver.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.saveOrUpdateKakaoUser(userInfoJson);

        // Then
        assertNotNull(result);
        assertEquals(kakaoId, result.getId());
        assertEquals(nickname, result.getName());
        assertEquals(nickname, result.getName());
        assertEquals(email, result.getEmail());
        assertEquals(UserStatus.ACTIVE, result.getStatusCode());
        assertNotEquals(refreshToken, result.getRefreshToken());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("구글 로그인 테스트")
    void test8() {
        // Given
        String userInfoJson = "{ \"id\": 12345, \"properties\": { \"nickname\": \"testUser\" } }";
        long googleId = 12345L;
        String nickname = "testUser";
        String email = googleId + "aA@naver.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.saveOrUpdateKakaoUser(userInfoJson);

        // Then
        assertNotNull(result);
        assertEquals(googleId, result.getId());
        assertEquals(nickname, result.getName());
        assertEquals(nickname, result.getName());
        assertEquals(email, result.getEmail());
        assertEquals(UserStatus.ACTIVE, result.getStatusCode());
        assertNotEquals(refreshToken, result.getRefreshToken());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("깃헙 로그인 테스트")
    void test9() {
        // Given
        String userInfoJson = "{ \"id\": 12345, \"properties\": { \"nickname\": \"testUser\" } }";
        long githubId = 12345L;
        String nickname = "testUser";
        String email = githubId + "aA@naver.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.saveOrUpdateKakaoUser(userInfoJson);

        // Then
        assertNotNull(result);
        assertEquals(githubId, result.getId());
        assertEquals(nickname, result.getName());
        assertEquals(nickname, result.getName());
        assertEquals(email, result.getEmail());
        assertEquals(UserStatus.ACTIVE, result.getStatusCode());
        assertNotEquals(refreshToken, result.getRefreshToken());

        verify(userRepository, times(1)).save(user);
    }
}