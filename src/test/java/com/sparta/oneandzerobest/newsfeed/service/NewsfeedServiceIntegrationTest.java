package com.sparta.oneandzerobest.newsfeed.service;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.entity.UserStatus;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.comment.dto.CommentRequestDto;
import com.sparta.oneandzerobest.comment.entity.Comment;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import com.sparta.oneandzerobest.newsfeed.repository.NewsfeedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class NewsfeedServiceIntegrationTest {

    @Autowired
    private NewsfeedService newsfeedService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NewsfeedRepository newsfeedRepository;

    User user;
    Newsfeed newsfeed;
    Newsfeed newsfeed2;

    @BeforeEach
    void setUp() {
        user = new User("testUsername", "testPassword!", "testName", "testEmail@example.com", UserStatus.ACTIVE);
        userRepository.save(user);

        newsfeed = new Newsfeed(user.getId(), "this is newsfeed1 test");
        newsfeed2 = new Newsfeed(user.getId(), "this is newsfeed2 test");
        newsfeedRepository.save(newsfeed);
        newsfeedRepository.save(newsfeed2);
    }

    @Test
    @DisplayName("게시글 작성 테스트")
    void test1() {
        // Given
        NewsfeedRequestDto newsfeedRequestDto = new NewsfeedRequestDto("this is add test");

        // When
        NewsfeedResponseDto newsfeedResponseDto = newsfeedService.postContent(user.getUsername(), newsfeedRequestDto).getBody();

        // Then
        assertNotNull(newsfeedResponseDto);
        assertEquals(user.getId(), newsfeedResponseDto.getUserid());
        assertEquals(newsfeedRequestDto.getContent(), newsfeedResponseDto.getContent());
    }

    @Test
    @DisplayName("게시글 조회 테스트")
    void test2() {
        // When
        ResponseEntity<Page<NewsfeedResponseDto>> response = newsfeedService.getAllContents(
                0, 10, true, false, null, null);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("게시물 수정 테스트")
    void test3() {
        // Given
        NewsfeedRequestDto newsfeedRequestDto = new NewsfeedRequestDto("newsfeed update test");

        // When
        newsfeedService.putContent(user.getUsername(), newsfeed.getId(), newsfeedRequestDto);

        // Then
        assertEquals(newsfeedRequestDto.getContent(), newsfeed.getContent());
    }

    @Test
    @DisplayName("게시물 삭제 테스트")
    void test4() {
        // When
        ResponseEntity<Long> result = newsfeedService.deleteContent(user.getUsername(), newsfeed.getId());

        // Then
        assertEquals(newsfeed.getId(), result.getBody());
    }
}