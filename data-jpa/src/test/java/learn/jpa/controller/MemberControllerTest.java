package learn.jpa.controller;

import learn.jpa.model.domain.Member;
import learn.jpa.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static learn.jpa.model.domain.Member.createMember;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {
    @Autowired MockMvc mvc;
    @Autowired MemberRepository memberRepository;

    @Test
    void getMember() throws Exception {
        // given
        Member member = createMember("siro", 29);
        memberRepository.save(member);

        // when
        ResultActions resultActions = mvc.perform(get("/members/" + member.getId()));

        // then
        resultActions.andDo(print())
                     .andExpect(status().isOk())
                     .andExpect(content().string(containsString("1")))
                     .andExpect(content().string(containsString("siro")))
                     .andExpect(content().string(containsString("29")));
    }
}
