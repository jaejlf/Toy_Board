package com.filling.good.domain.user.controller;

import com.filling.good.domain.user.service.JwtTokenProvider;
import com.filling.good.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@DisplayName("User 컨트롤러")
class UserControllerTest extends CommonControllerTest {

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @DisplayName("현재 로그인된 유저 정보 조회")
    @Test
    void getUserInfo() throws Exception {
        //given & when
        ResultActions actions = mockMvc.perform(get("/user/info")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("/user/" + "getUserInfo",
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data.userId").description("유저 고유 아이디"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.nickname").description("닉네임"),
                                fieldWithPath("data.name").description("이름"),
                                fieldWithPath("data.fillPercent").description("Fill 퍼센테이지"),
                                fieldWithPath("data.job").description("직업"),
                                fieldWithPath("data.authProvider").description("가입 경로 (DEFAULT/GOOGLE)")
                        ))
                );
    }

}