package dev.yoon.springguide.domain.member.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yoon.springguide.domain.member.dao.MemberFindDao;
import dev.yoon.springguide.domain.member.domain.Member;
import dev.yoon.springguide.domain.member.domain.MemberBuilder;
import dev.yoon.springguide.domain.member.dto.SignUpRequest;
import dev.yoon.springguide.domain.member.dto.SignUpRequestBuilder;
import dev.yoon.springguide.domain.member.model.Email;
import dev.yoon.springguide.domain.member.model.Name;
import dev.yoon.springguide.domain.member.service.MemberProfileService;
import dev.yoon.springguide.domain.member.service.MemberSearchService;
import dev.yoon.springguide.domain.member.service.MemberSignUpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations="classpath:application-test.yml")
public class MemberMockApiTest  {

    @InjectMocks
    private MemberApi memberApi;

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private MemberSignUpService memberSignUpService;

    @MockBean
    private MemberFindDao memberFindDao;

    @MockBean
    private MemberProfileService memberProfileService;

    @MockBean
    private MemberSearchService memberSearchService;


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberApi)
                .build();
    }


    @Test
    public void 회원가입_성공() throws Exception {
        //given
        final Member member = MemberBuilder.build();
        final Email email = member.getEmail();
        final Name name = member.getName();
        final SignUpRequest dto = SignUpRequestBuilder.build(email, name);

        given(memberSignUpService.doSignUp(any())).willReturn(member);

        //when
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dto);

        final ResultActions actions = mockMvc.perform(
                post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("email.value").value(email.getValue()))
                .andExpect(jsonPath("email.host").value(email.getHost()))
                .andExpect(jsonPath("email.id").value(email.getId()))
                .andExpect(jsonPath("name.first").value(name.getFirst()))
                .andExpect(jsonPath("name.middle").value(name.getMiddle()))
                .andExpect(jsonPath("name.last").value(name.getLast()))
                .andExpect(jsonPath("name.fullName").value(name.getFullName()))
        ;
    }

    @Test
    public void 회원가입_유효하지않은_입력값() throws Exception {
        //given
        final Email email = Email.of("asdasd@d");
        final Name name = Name.builder().build();
        final SignUpRequest dto = SignUpRequestBuilder.build(email, name);
        final Member member = MemberBuilder.build();

        given(memberSignUpService.doSignUp(any())).willReturn(member);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dto);
        //when

        final ResultActions actions = mockMvc.perform(
                post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        //then
        actions
                .andExpect(status().isBadRequest())
        ;

    }

//    private ResultActions requestSignUp(SignUpRequest dto) throws Exception {
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json = objectMapper.writeValueAsString(dto);
//
//        ResultActions perform = mockMvc.perform(
//                post("/members")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json));
//
//    }
}