package dev.yoon.jpa.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yoon.jpa.dto.BoardDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(BoardController.class)
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

//    @MockBean
//    private BoardService boardService;

    @Test
    public void createBoard() throws Exception {
        //given
        BoardDto boardDto = new BoardDto();
        boardDto.setName("notice");

        //when
        final ResultActions actions = mockMvc.perform(post("/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(boardDto)))
                .andDo(print());
        //then
        actions.andExpectAll(
                // 2xx번대 성공
                status().is2xxSuccessful(), // status확인
                content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON), // 리턴값 확인
                jsonPath("$.name", is("notice"))

        );
    }

    private byte[] toJson(Object object) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);

    }

}
