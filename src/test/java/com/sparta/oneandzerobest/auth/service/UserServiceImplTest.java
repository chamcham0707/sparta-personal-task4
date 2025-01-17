package com.sparta.oneandzerobest.auth.service;

import com.sparta.oneandzerobest.auth.config.JwtConfig;
import com.sparta.oneandzerobest.auth.email.service.EmailService;
import com.sparta.oneandzerobest.auth.entity.SignupRequest;
import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.entity.UserStatus;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.exception.IdPatternException;
import com.sparta.oneandzerobest.exception.InfoNotCorrectedException;
import com.sparta.oneandzerobest.exception.PasswordPatternException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private JwtConfig jwtConfig;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, emailService, redisTemplate, jwtUtil);
    }

    @Test
    @DisplayName("아이디 유효성 예외 테스트 ")
    void test1() {
        // Given
        SignupRequest signupRequest = new SignupRequest("testUser", "password", "test@email.com", false, "");

        // When
        Exception exception = assertThrows(IdPatternException.class, () -> userService.signup(signupRequest));

        // Then
        assertEquals(
                "아이디는 최소 10자 이상, 20자 이하이며 알파벳 대소문자와 숫자로 구성되어야 합니다.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("비밀번호 유효성 예외 테스트")
    void test2() {
        // Given
        SignupRequest signupRequest = new SignupRequest("Testusername", "password", "test@email.com", false, "");

        // When
        Exception exception = assertThrows(PasswordPatternException.class, () -> userService.signup(signupRequest));

        // Then
        assertNotNull(exception);
    }

    @Test
    @DisplayName("중복 ID 회원 예외 테스트")
    void test3() {
        // Given
        String username = "ExistUsername";
        SignupRequest signupRequest = new SignupRequest(username, "Password11!", "test@email.com", false, "");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User(username, "password", "user1", "user1@email.com", UserStatus.ACTIVE)));

        // When
        Exception exception = assertThrows(InfoNotCorrectedException.class, () -> userService.signup(signupRequest));

        // Then
        verify(userRepository).findByUsername(username);
        assertEquals("중복된 사용자 ID가 존재합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("중복된 이메일 예외 테스트")
    void test4() {
        // Given
        String email = "test@email.com";
        SignupRequest signupRequest = new SignupRequest("TestUsername", "Password11!", email, false, "");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User("username", "password", "user1" , email, UserStatus.ACTIVE)));

        // When
        Exception exception = assertThrows(InfoNotCorrectedException.class, () -> userService.signup(signupRequest));

        // Then
        verify(userRepository).findByEmail(email);
        assertEquals("중복된 이메일이 존재합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("updateEmail() : 이메일 업데이트 테스트")
    void test5() {
        // Given
        String authId = "testUser";
        SignupRequest signupRequest = new SignupRequest(authId, "password", "update@email.com", false, "");
        User user = new User(signupRequest.getUsername(), signupRequest.getPassword(), "testUser", "notchange@email.com", UserStatus.UNVERIFIED);
        when(userRepository.findByUsername(authId)).thenReturn(Optional.of(user));

        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        userService.updateEmail(signupRequest);

        // Then
        verify(userRepository).save(user);
        assertEquals(signupRequest.getEmail(), user.getEmail());
    }
}