package com.sparta.oneandzerobest.comment.dto;

import com.sparta.oneandzerobest.comment.entity.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentResponseDtoTest {

    @Test
    @DisplayName("formatDateTime() 테스트")
    void test1() {
        // Given
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        LocalDateTime now = LocalDateTime.now();
        String formatDateTime = null;
        try {
            Method method = CommentResponseDto.class.getDeclaredMethod("formatDateTime", LocalDateTime.class);
            method.setAccessible(true);

            // When
            formatDateTime = (String) method.invoke(commentResponseDto, now);
        } catch (Exception e) {
            fail("fail - error message: " + e.getMessage());
        }

        // Then
        assertNotEquals(now, formatDateTime);
    }

}