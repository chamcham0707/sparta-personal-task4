package com.sparta.oneandzerobest.newsfeed.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NewsfeedRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("NewsfeedRequestDto 성공 테스트")
    void test1() {
        // Given
        NewsfeedRequestDto newsfeedRequestDto = new NewsfeedRequestDto();
        String content = "this is test";

        try {
            Field field = NewsfeedRequestDto.class.getDeclaredField("content");
            field.setAccessible(true);
            field.set(newsfeedRequestDto, content);
        } catch (Exception e) {
            fail("fail - error message: " + e.getMessage());
        }

        // When
        Set<ConstraintViolation<NewsfeedRequestDto>> violations = validator.validate(newsfeedRequestDto);

        // Then
        assertEquals(content, newsfeedRequestDto.getContent());
    }

    @Test
    @DisplayName("NewsfeedRequestDto content 내용 없음 예외 테스트")
    void test2() {
        // Given
        NewsfeedRequestDto newsfeedRequestDto = new NewsfeedRequestDto();
        String content = "";

        try {
            Field field = NewsfeedRequestDto.class.getDeclaredField("content");
            field.setAccessible(true);
            field.set(newsfeedRequestDto, content);
        } catch (Exception e) {
            fail("fail - error message: " + e.getMessage());
        }

        // When
        Set<ConstraintViolation<NewsfeedRequestDto>> violations = validator.validate(newsfeedRequestDto);

        // Then
        assertEquals("내용이 비어있습니다.", violations.iterator().next().getMessage());
    }
}