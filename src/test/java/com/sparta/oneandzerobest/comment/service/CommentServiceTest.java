package com.sparta.oneandzerobest.comment.service;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.comment.dto.CommentRequestDto;
import com.sparta.oneandzerobest.comment.dto.CommentResponseDto;
import com.sparta.oneandzerobest.comment.entity.Comment;
import com.sparta.oneandzerobest.comment.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
    FixtureMonkey fixtureMonkey;
    CommentRequestDto commentRequestDto;

    User user;

    Comment comment;

    @BeforeEach
    void setUp() {
        fixtureMonkey = FixtureMonkey.builder()
                .defaultNotNull(Boolean.TRUE)
                .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
                .build();

        commentRequestDto = fixtureMonkey.giveMeOne(CommentRequestDto.class);
        user = fixtureMonkey.giveMeOne(User.class);
        comment = fixtureMonkey.giveMeBuilder(Comment.class)
                .set("userId", user.getId())
                .set("content", commentRequestDto.getContent())
                .sample();
    }

    @Test
    @DisplayName("댓글 등록 테스트")
    void test1() {
        // Given
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // When
        CommentResponseDto commentResponseDto = commentService.addComment(comment.getNewsfeedId(), commentRequestDto, user.getUsername());

        // Then
        assertEquals(comment.getContent(), commentResponseDto.getContent());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
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
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.findByIdAndNewsfeedIdAndUserId(comment.getId(), comment.getNewsfeedId(), user.getId())).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        // When
        CommentResponseDto commentResponseDto = commentService.updateComment(comment.getNewsfeedId(), comment.getId(), commentRequestDto, user.getUsername());

        // Then
        assertEquals(comment.getContent(), commentResponseDto.getContent());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, times(1)).findByIdAndNewsfeedIdAndUserId(comment.getId(), comment.getNewsfeedId(), user.getId());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void test4() {
        // Given
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.findByIdAndNewsfeedIdAndUserId(comment.getId(), comment.getNewsfeedId(), user.getId())).thenReturn(Optional.of(comment));

        // When
        commentService.deleteComment(comment.getNewsfeedId(), comment.getId(), user.getUsername());

        // Then
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, times(1)).findByIdAndNewsfeedIdAndUserId(comment.getId(), comment.getNewsfeedId(), user.getId());
        verify(commentRepository, times(1)).delete(comment);
    }
}