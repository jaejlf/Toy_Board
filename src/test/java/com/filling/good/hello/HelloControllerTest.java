package com.filling.good.hello;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filling.good.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.SecurityConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HelloController.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureMockMvc
@Import(TestConfig.class)
@AutoConfigureRestDocs(uriHost = "FILLing-GOOD-URL", uriPort = 8080)
@DisplayName("Hello")
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("hello 테스트")
    public void hello() throws Exception {

        //given
        Map<String, String> map = new HashMap<>();
        map.put("msg", "hello");

        String content = objectMapper.writeValueAsString(map);

        //when
        ResultActions actions = mockMvc.perform(get("/hello"));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(content().string(content))
                .andDo(print())
                .andDo(document("hello",
                        responseFields(
                                fieldWithPath("msg").description("항상 \"hello\"")
                        ))
                );

    }

    @Test
    @DisplayName("bye 테스트")
    public void bye() throws Exception {

        //given
        String input = "bye";

        //when
        ResultActions actions = mockMvc.perform(post("/bye/{msg}", input));

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("msg").value(input))
                .andDo(print())
                .andDo(document("bye",
                        pathParameters(
                                parameterWithName("msg").description("사용자 입력 메세지")
                        ),
                        responseFields(
                                fieldWithPath("msg").description("사용자 입력 메세지")
                        ))
                );

    }
}