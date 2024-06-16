package com.sparta.oneandzerobest.comment.dto;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CommentResponseDtoTest {
    FixtureMonkey fixtureMonkey;

    @BeforeEach
    void setUp() {
        fixtureMonkey = FixtureMonkey.builder()
                .defaultNotNull(Boolean.TRUE)
                .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
                .build();
    }

    @Test
    @DisplayName("formatDateTime() 테스트")
    void test1() {
        // Given
        CommentResponseDto commentResponseDto = fixtureMonkey.giveMeOne(CommentResponseDto.class);
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