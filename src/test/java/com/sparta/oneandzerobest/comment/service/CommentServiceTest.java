package com.sparta.oneandzerobest.comment.service;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.entity.UserStatus;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.comment.dto.CommentRequestDto;
import com.sparta.oneandzerobest.comment.dto.CommentResponseDto;
import com.sparta.oneandzerobest.comment.entity.Comment;
import com.sparta.oneandzerobest.comment.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("댓글 등록 테스트")
    void test1() {
        // Given
        Long newsfeedId = 1L;
        CommentRequestDto commentRequestDto = new CommentRequestDto(newsfeedId, "this is test comment");
        String username = "testUsername";
        User user = new User(1L, username, "testPassword!", "testName", "test@example.com", "test!", UserStatus.ACTIVE, "testRefreshToken", LocalDateTime.now(), LocalDateTime.now(), null);
        Comment comment = new Comment(1L, newsfeedId, user.getId(), commentRequestDto.getContent());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // When
        CommentResponseDto commentResponseDto = commentService.addComment(newsfeedId, commentRequestDto, username);

        // Then
        assertEquals(comment.getContent(), commentResponseDto.getContent());

        verify(userRepository, times(1)).findByUsername(username);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("뉴스피트의 모든 댓글 조회 테스트")
    void test2() {
        // Given
        Long newsfeedId = 1L;
        List<Comment> comments = new ArrayList<>(Arrays.asList(
                new Comment(1L, 1L, 1L, "test1 comment"),
                new Comment(2L, 1L, 1L, "test2 comment"),
                new Comment(3L, 1L, 3L, "test3 comment")
        ));

        when(commentRepository.findByNewsfeedId(newsfeedId)).thenReturn(comments);

        // When
        List<CommentResponseDto> result = commentService.getAllComments(newsfeedId);

        // Then
        assertEquals(comments.size(), result.size());
        assertEquals(comments.get(0).getContent(), result.get(0).getContent());
        assertEquals(comments.get(1).getContent(), result.get(1).getContent());
        assertEquals(comments.get(2).getContent(), result.get(2).getContent());

        verify(commentRepository, times(1)).findByNewsfeedId(newsfeedId);
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void test3() {
        // Given
        Long newsfeedId = 1L;
        Long commentId = 1L;
        CommentRequestDto commentRequestDto = new CommentRequestDto(newsfeedId, "comment1");
        User user = new User(1L, "testUsername", "testPassword!", "testName", "test@example.com", "test1111", UserStatus.ACTIVE, "testRefreshToken", LocalDateTime.now(), LocalDateTime.now(), null);
        Comment comment = new Comment(commentId, newsfeedId, user.getId(), commentRequestDto.getContent());

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.findByIdAndNewsfeedIdAndUserId(commentId, newsfeedId, user.getId())).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        // When
        CommentResponseDto commentResponseDto = commentService.updateComment(newsfeedId, commentId, commentRequestDto, user.getUsername());

        // Then
        assertEquals(comment.getContent(), commentResponseDto.getContent());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, times(1)).findByIdAndNewsfeedIdAndUserId(commentId, newsfeedId, user.getId());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void test4() {
        // Given
        Long newsfeedId = 1L;
        Long commentId = 1L;
        User user = new User(1L, "testUsername", "testPassword!", "testName", "test@example.com", "test1111", UserStatus.ACTIVE, "testRefreshToken", LocalDateTime.now(), LocalDateTime.now(), null);
        Comment comment = new Comment(1L, newsfeedId, user.getId(), "comment1");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.findByIdAndNewsfeedIdAndUserId(commentId, newsfeedId, user.getId())).thenReturn(Optional.of(comment));

        // When
        commentService.deleteComment(newsfeedId, commentId, user.getUsername());

        // Then
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, times(1)).findByIdAndNewsfeedIdAndUserId(commentId, newsfeedId, user.getId());
        verify(commentRepository, times(1)).delete(comment);
    }
}