package dev.yoon.jpa.controller;

import dev.yoon.jpa.dto.PostDto;
import dev.yoon.jpa.service.PostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UnitTest
 */
@RunWith(SpringRunner.class)
@WebMvcTest(PostController.class)
/**
 * Mvc를 테스트함을 알려주는 어노테이션
 * Spring의 bean으로 등록된 것중 Controller, ControllerAdvice와 같은 것들이 실제로 실행했을때 처럼 작동
 * Component, Service bean들은 작동x
 */
public class PostControllerTest {


    // Test를 하기 위해 Mock를 생성
    // MockMvc는 마치 http클라이언트 인척하는 클래스
    // @MockBean: 해당 클래스를 따라하는 객체 -> 실제로는 생성x, Bean객체로 ioc컨테이너에 등록됨
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Test
    public void readPost() throws Exception {
        // given: 어떤 데이터가 준비가 되어있다. -> read하기 위해서 id를 알아야함
        // PostEntity가 존재할 때
        final int id = 10;
        PostDto testDto = new PostDto();
        testDto.setId(id);
        testDto.setTitle("Unit Title");
        testDto.setContent("Unit Content");
        testDto.setWriter("Unit");

        // 기능을 부여하는 라이브러리: mockito
        // given(함수호출).willReturn(호출결과)
        given(postService.readPost(id)).willReturn(testDto);

        // when: 어떠한 행위가 일어났을떄(함수 호출 등)
        // 경로에 GET 요청이 오면
        // andDo: 결과를 받으면 할 행동
        final ResultActions actions = mockMvc.perform(get("/post/{id}",id))
                .andDo(print());

        // then: 행위에 대한 결과
        // PostDto 반환
        // 결과로 기대값
        actions.andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.title", is("Unit Title")),
                jsonPath("$.content", is("Unit Content")),
                jsonPath("$.writer", is("Unit"))
        );
    }

    @Test
    public void readPostAll() throws Exception {

        // given
        PostDto post1 = new PostDto();
        post1.setTitle("title 1");
        post1.setContent("test");
        post1.setWriter("test");

        PostDto post2 = new PostDto();
        post2.setTitle("title 2");
        post2.setContent("test2");
        post2.setWriter("test2");

        List<PostDto> readAllPost = Arrays.asList(post1, post2);
        given(postService.readPostAll()).willReturn(readAllPost);

        // when
        final ResultActions actions = mockMvc.perform(get("/post"))
                .andDo(print());

        // then
        // $: json객체 전체
        actions.andExpectAll(
                status().isOk(),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                jsonPath("$", hasSize(readAllPost.size()))
        );

    }
}