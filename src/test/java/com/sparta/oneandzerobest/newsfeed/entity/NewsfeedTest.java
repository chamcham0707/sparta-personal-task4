package com.sparta.oneandzerobest.newsfeed.entity;

import com.sparta.oneandzerobest.newsfeed_like.entity.NewsfeedLike;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class NewsfeedTest {

    Newsfeed newsfeed;
    List<NewsfeedLike> newsfeedLikeList;

    @BeforeEach
    void setUp() {
        newsfeed = new Newsfeed();
        newsfeedLikeList = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            NewsfeedLike newsfeedLike = new NewsfeedLike();
            newsfeedLikeList.add(newsfeedLike);
            newsfeed.setNewsfeedLike(newsfeedLike);
        }
    }

    @Test
    @DisplayName("setNewsfeedLike() 테스트")
    void test1() {
        // Then
        assertEquals(newsfeed.getNewsfeedLikeList(), newsfeedLikeList);
        assertEquals(newsfeed.getLikeCount(), newsfeedLikeList.size());
    }

    @Test
    @DisplayName("removeNewsfeedLike() 테스트")
    void test2() {
        // When
        newsfeed.removeNewsfeedLike(newsfeedLikeList.get(4));
        newsfeed.removeNewsfeedLike(newsfeedLikeList.get(5));
        newsfeed.removeNewsfeedLike(newsfeedLikeList.get(8));

        // Then
        assertFalse(newsfeed.getNewsfeedLikeList().contains(newsfeedLikeList.get(4)));
        assertFalse(newsfeed.getNewsfeedLikeList().contains(newsfeedLikeList.get(5)));
        assertFalse(newsfeed.getNewsfeedLikeList().contains(newsfeedLikeList.get(8)));

        assertEquals(7, newsfeed.getLikeCount());
    }
}