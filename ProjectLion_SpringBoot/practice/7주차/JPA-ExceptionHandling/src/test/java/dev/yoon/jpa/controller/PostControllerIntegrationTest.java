package dev.yoon.jpa.controller;

import dev.yoon.jpa.JpaApplication;
import dev.yoon.jpa.entity.PostEntity;
import dev.yoon.jpa.repository.PostRepository;
import dev.yoon.jpa.service.PostService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * IntegrationTest
 */
@RunWith(SpringRunner.class)
/**
 * WebMvcTest는 UnitTest용도, SpringBootTest는 스프링 부트 앱 전체를 테스트하는 용도
 * webEnvironment: 환경에 대한 속성
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = JpaApplication.class
)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class) // 보안 설정 해제
@AutoConfigureTestDatabase // database를 h2
public class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;


    // Test실행 전에 실행되는 함수
    @Before
    public void setEntities() {
        createTestPost("first post", "first title", "first writer");
        createTestPost("second post", "second title", "second writer");
        createTestPost("third post", "third title", "third writer");

    }

    private Long createTestPost(String title, String content, String writer) {

        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(title);
        postEntity.setContent(content);
        postEntity.setWriter(writer);

        return postRepository.save(postEntity).getId();
    }

    @Test
    public void givenVaildId_whenReadPost_then200() throws Exception {
        // given
        Long id = createTestPost("Read Post", "Created on ReadPost()", "readTest");

        // when
        final ResultActions actions = mockMvc.perform(get("/post/{id}", id))
                .andDo(print());

        // then
        actions.andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.title", is("Read Post")),
                jsonPath("$.content", is("Created on ReadPost()")),
                jsonPath("$.writer", is("readTest"))

        );



    }


}
