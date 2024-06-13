package com.sparta.oneandzerobest.comment.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    @DisplayName("setModifiedAt() 테스트")
    void test1() {
        // Given
        Comment comment = new Comment();
        LocalDateTime now = LocalDateTime.now();

        // When
        comment.setModifiedAt(now);

        // Then
        assertEquals(now, comment.getModifiedAt());
    }
}