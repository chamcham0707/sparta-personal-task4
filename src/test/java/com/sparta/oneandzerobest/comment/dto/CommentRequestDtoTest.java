package com.sparta.oneandzerobest.comment.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CommentRequestDtoTest {

    private Validator validator;

    CommentRequestDto commentRequestDto;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        commentRequestDto = new CommentRequestDto();
    }

    @Test
    @DisplayName("CommentRequestDto 성공 테스트")
    void test1() {
        // Given
        commentRequestDto.setNewsfeedId(1L);
        commentRequestDto.setContent("test comment");

        // When
        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentRequestDto);

        // Then
        assertTrue(violations.isEmpty());
    }


    @Test
    @DisplayName("CommentRequestDto 뉴스피드 Id Null 예외 테스트")
    void test2() {
        // Given
        commentRequestDto.setContent("newsfeedId null test");

        // When
        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentRequestDto);

        // Then
        assertEquals("뉴스피드 ID는 필수입니다.", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("CommentRequestDto 댓글 공백 예외 테스트")
    void test3() {
        // Given
        commentRequestDto.setNewsfeedId(1L);
        commentRequestDto.setContent("");

        // When
        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentRequestDto);

        // Then
        assertEquals("댓글 내용은 공백일 수 없습니다.", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("CommentRequestDto 댓글 최대 글자 예외 테스트")
    void test4() {
        // Given
        commentRequestDto.setNewsfeedId(1L);
        commentRequestDto.setContent("kldsfjdaslkfjdslkfjdsalfkjadsl;kfjads;lkfjads;lfkjad;lskfjadslkfjadslkfjads;lkfjads;lkfjsad;lkfjas;dlkfja;dslkfjsad;lkfjads;lkfjads;lkfjasdl;kfjas;dlkfjdsa;lkfjasdlkfjads;lkfjdsalkfjdslkfjcxmvn.,czxmvn,z.xmcvnmcxznvz.x,mcvnzx.c,mvnzx.c,mvnz.xc,mvnz,.cxmvnz.,");

        // When
        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentRequestDto);

        // Then
        assertEquals("댓글은 최대 255자까지 입력 가능합니다.", violations.iterator().next().getMessage());
    }
}