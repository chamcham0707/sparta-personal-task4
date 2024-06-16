package com.sparta.oneandzerobest.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.oneandzerobest.auth.config.SecurityConfig;
import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.entity.UserStatus;
import com.sparta.oneandzerobest.auth.service.UserDetailsServiceImpl;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.comment.controller.CommentController;
import com.sparta.oneandzerobest.comment.dto.CommentRequestDto;
import com.sparta.oneandzerobest.comment.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {CommentController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)
public class CommentMvcTest {
    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockBean
    private CommentService commentService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    private void mockUserSetup() {
        String username = "testUser";
        String password = "testPassword";
        User user = new User();
        org.springframework.security.core.userdetails.User testUser = new org.springframework.security.core.userdetails.User(username, password, user.getAuthorities());
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUser, "", testUser.getAuthorities());
    }

    @Test
    @DisplayName("댓글 등록 테스트")
    public void test1() throws Exception {
        // Given
        this.mockUserSetup();

        Long newsfeedId = 1L;
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setNewsfeedId(newsfeedId);
        commentRequestDto.setContent("this is test content");

        String postInfo = objectMapper.writeValueAsString(commentRequestDto);

        // When - Then
        mvc.perform(post("/newsfeed/{newsfeedId}/comment", newsfeedId)
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("모든 댓글 가져오기 테스트")
    public void test2() throws Exception {
        // Given
        this.mockUserSetup();

        Long newsfeedId = 1L;

        // When - Then
        mvc.perform(get("/newsfeed/{newsfeedId}/comment", newsfeedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk()) // 201 보내는지 확인
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    public void test3() throws Exception {
        // Given
        this.mockUserSetup();

        Long newsfeedId = 1L;
        Long commentId = 1L;
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setNewsfeedId(newsfeedId);
        commentRequestDto.setContent("this is test content");

        String postInfo = objectMapper.writeValueAsString(commentRequestDto);

        // When - Then
        mvc.perform(put("/newsfeed/{newsfeedId}/comment/{commentId}", newsfeedId, commentId)
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk()) // 201 보내는지 확인
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    public void test4() throws Exception {
        // Given
        this.mockUserSetup();

        Long newsfeedId = 1L;
        Long commentId = 1L;

        // When - Then
        mvc.perform(delete("/newsfeed/{newsfeedId}/comment/{commentId}", newsfeedId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk()) // 201 보내는지 확인
                .andDo(print());
    }
}
