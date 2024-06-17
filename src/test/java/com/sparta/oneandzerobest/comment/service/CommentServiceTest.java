package com.sparta.oneandzerobest.comment.service;

import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.entity.UserStatus;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.comment.dto.CommentRequestDto;
import com.sparta.oneandzerobest.comment.dto.CommentResponseDto;
import com.sparta.oneandzerobest.comment.entity.Comment;
import com.sparta.oneandzerobest.comment.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    CommentRequestDto commentRequestDto;

    User user;

    Comment comment;

    @BeforeEach
    void setUp() {
        commentRequestDto = new CommentRequestDto(1L, "this is commentRequestDto comment");

        user = new User("testUsername", "testPassword!", "testName", "testEmail@example.com", UserStatus.ACTIVE);
        userRepository.save(user);

        comment = new Comment(1L, user.getId(), "this is test");
        commentRepository.save(comment);
    }

    @Test
    @DisplayName("댓글 등록 테스트")
    void test1() {
        // When
        CommentResponseDto commentResponseDto = commentService.addComment(1L, commentRequestDto, user.getUsername());

        // Then
        assertEquals(commentRequestDto.getContent(), commentResponseDto.getContent());
    }

    @Test
    @DisplayName("뉴스피드의 모든 댓글 조회 테스트")
    void test2() {
        // Given
        Long newsfeedId = 1L;

        // When
        List<CommentResponseDto> result = commentService.getAllComments(newsfeedId);

        // Then
        assertEquals(1, result.size());
        assertEquals(comment.getContent(), result.get(0).getContent());
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void test3() {
        // Given
        CommentRequestDto updateRequestDto = new CommentRequestDto(comment.getNewsfeedId(), "update comment");

        // When
        CommentResponseDto commentResponseDto = commentService.updateComment(comment.getNewsfeedId(), comment.getId(), updateRequestDto, user.getUsername());

        // Then
        assertEquals(updateRequestDto.getContent(), commentResponseDto.getContent());
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void test4() {
        // When
        commentService.deleteComment(comment.getNewsfeedId(), comment.getId(), user.getUsername());

        // Then
        assertEquals(Optional.empty(), commentRepository.findById(comment.getId()));
    }
}