package com.sparta.oneandzerobest.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.oneandzerobest.auth.config.SecurityConfig;
import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.entity.UserStatus;
import com.sparta.oneandzerobest.auth.service.UserDetailsServiceImpl;
import com.sparta.oneandzerobest.auth.util.JwtUtil;
import com.sparta.oneandzerobest.newsfeed.controller.NewsfeedController;
import com.sparta.oneandzerobest.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.oneandzerobest.newsfeed.service.NewsfeedService;
import com.sparta.oneandzerobest.s3.service.ImageService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {NewsfeedController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)
public class NewsfeedMvcTest {
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
    private NewsfeedService newsfeedService;

    @MockBean
    private ImageService imageService;

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
    @DisplayName("뉴스피드 생성 테스트")
    public void test1() throws Exception {
        // Given
        this.mockUserSetup();

        NewsfeedRequestDto newsfeedRequestDto = new NewsfeedRequestDto("this is test");

        String postInfo = objectMapper.writeValueAsString(newsfeedRequestDto);

        // When - Then
        mvc.perform(post("/newsfeed")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("뉴스피드 조회 테스트")
    public void test2() throws Exception {
        // Given
        this.mockUserSetup();

        int page = 1;
        int size = 10;
        boolean isAsc = true;
        boolean like = false;
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();

        // When - Then
        mvc.perform(get("/newsfeed")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("isASC", String.valueOf(isAsc))
                        .param("like", String.valueOf(like))
                        .param("startTime", startTime.toString())
                        .param("endTime", endTime.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("뉴스피드 수정 테스트")
    public void test3() throws Exception {
        // Given
        this.mockUserSetup();

        Long newsfeedId = 1L;
        NewsfeedRequestDto newsfeedRequestDto = new NewsfeedRequestDto("this is test");

        String postInfo = objectMapper.writeValueAsString(newsfeedRequestDto);

        // When - Then
        mvc.perform(put("/newsfeed/{id}", newsfeedId)
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("뉴스피드 삭제 테스트")
    public void test4() throws Exception {
        // Given
        this.mockUserSetup();

        Long newsfeedId = 1L;

        // When - Then
        mvc.perform(delete("/newsfeed/{id}", newsfeedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("뉴스피드 이미지 업로드 테스트")
    public void test5() throws Exception {
        // Given
        this.mockUserSetup();

        Long newsfeedId = 1L;
        String token = "Bearer testToken";
        String fileName = "test-image.jpg";
        MediaType mediaType = MediaType.IMAGE_JPEG;

        byte[] content = "mock image content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", fileName, mediaType.toString(), content);

        // When - Then
        mvc.perform(MockMvcRequestBuilders.multipart("/newsfeed/media")
                        .file(file)
                        .param("id", newsfeedId.toString())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("뉴스피드 이미지 수정 테스트")
    public void test6() throws Exception {
        // Given
        this.mockUserSetup();

        Long newsfeedId = 1L;
        Long fileId = 1L;
        String token = "Bearer testToken";
        String fileName = "test-image.jpg";
        MediaType mediaType = MediaType.IMAGE_JPEG;

        byte[] content = "mock image content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", fileName, mediaType.toString(), content);

        // When - Then
        mvc.perform(MockMvcRequestBuilders.multipart("/newsfeed/media")
                        .file(file)
                        .param("id", newsfeedId.toString())
                        .param("fileId", fileId.toString())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
}
