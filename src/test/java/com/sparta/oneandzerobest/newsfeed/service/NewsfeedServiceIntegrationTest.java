package com.sparta.oneandzerobest.newsfeed.service;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.entity.UserStatus;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import com.sparta.oneandzerobest.newsfeed.repository.NewsfeedRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class NewsfeedServiceIntegrationTest {

    @Autowired
    private NewsfeedService newsfeedService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private NewsfeedRepository newsfeedRepository;

    @Test
    @DisplayName("게시글 작성 테스트")
    void test1() {
        // Given
        String username = "testUsername";
        String content = "this is test";
        Long userId = 1L;
        NewsfeedRequestDto newsfeedRequestDto = new NewsfeedRequestDto(content);
        User user = new User(userId, username, "testPassword!", "testName", "test@example.com", "test!!!", UserStatus.ACTIVE, "testRefreshToken", LocalDateTime.now(), LocalDateTime.now(), null);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(newsfeedRepository.save(any(Newsfeed.class))).thenReturn(new Newsfeed(userId, content));

        // When
        NewsfeedResponseDto newsfeedResponseDto = newsfeedService.postContent(username, newsfeedRequestDto).getBody();

        // Then
        assertNotNull(newsfeedResponseDto);
        assertEquals(user.getId(), newsfeedResponseDto.getUserid());
        assertEquals(newsfeedRequestDto.getContent(), newsfeedResponseDto.getContent());
    }

    @Test
    @DisplayName("게시글 조회 테스트")
    void test2() {
        // Given
        int page = 0;
        int size = 10;
        boolean isASC = true;
        boolean like = false;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        List<Newsfeed> newsfeeds = Arrays.asList(
                new Newsfeed(1L, "Content1"),
                new Newsfeed(2L, "Content2")
        );

        Page<Newsfeed> newsfeedPage = new PageImpl<>(newsfeeds);
        when(newsfeedRepository.findAll(any(Pageable.class))).thenReturn(newsfeedPage);

        // When
        ResponseEntity<Page<NewsfeedResponseDto>> response = newsfeedService.getAllContents(
                page, size, isASC, like, startTime, endTime);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(newsfeeds.size());
        verify(newsfeedRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("게시물 수정 테스트")
    void test3() {
        // Given
        String username = "testUsername";
        Long contentId = 1L;
        String content = "this is test";
        String updateContent = "update test success";
        NewsfeedRequestDto newsfeedRequestDto = new NewsfeedRequestDto(updateContent);
        User user = new User(1L, username, "testPassword!", "testName", "test@example.com", "test!!", UserStatus.ACTIVE, "testRefreshToken", LocalDateTime.now(), LocalDateTime.now(), null);
        Newsfeed newsfeed = new Newsfeed(user.getId(), content);

        when(newsfeedRepository.findById(contentId)).thenReturn(Optional.of(newsfeed));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        newsfeedService.putContent(username, contentId, newsfeedRequestDto);

        // Then
        assertEquals(updateContent, newsfeed.getContent());

        verify(userRepository, times(1)).findByUsername(username);
        verify(newsfeedRepository, times(1)).findById(contentId);
    }

    @Test
    @DisplayName("게시물 삭제 테스트")
    void test4() {
        // Given
        String username = "testUsername";
        Long contentId = 1L;
        User user = new User(1L, "testUsername", "testPassword1", "testName", "test@example.com", "test!!!", UserStatus.ACTIVE, "testRefreshToken", LocalDateTime.now(), LocalDateTime.now(), null);
        Newsfeed newsfeed = new Newsfeed(user.getId(), "this is test");

        when(newsfeedRepository.findById(contentId)).thenReturn(Optional.of(newsfeed));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        ResponseEntity<Long> result = newsfeedService.deleteContent(username, contentId);

        // Then
        assertEquals(newsfeed.getId(), result.getBody());

        verify(newsfeedRepository, times(1)).findById(contentId);
        verify(userRepository, times(1)).findByUsername(username);
    }
}