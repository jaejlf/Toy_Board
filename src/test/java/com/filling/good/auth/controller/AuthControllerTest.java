package com.filling.good.auth.controller;

import com.filling.good.common.CommonTest;
import com.filling.good.common.TestConfig;
import com.filling.good.auth.dto.request.AuthRequest;
import com.filling.good.auth.dto.response.AuthResponse;
import com.filling.good.auth.service.AuthService;
import com.filling.good.user.enumerate.Job;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import(TestConfig.class)
@DisplayName("Auth")
class AuthControllerTest extends CommonTest {

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("회원가입에 성공한다.")
    void join_test() throws Exception {

        //given
        AuthRequest authRequest = new AuthRequest(
                "fill@naver.com",
                "secret1234",
                "필링굿",
                "학생"
        );

        given(authService.save(any())).willReturn(joinWillReturnDto(authRequest));

        //when
        ResultActions actions = mockMvc.perform(post("/auth/join")
                .content(objectMapper.writeValueAsString(authRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("data").exists())
                .andDo(print())
                .andDo(document("auth-join",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("jobValue").description("직업 (학생/리크루터/프리랜서/개발자/기타)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드 : 201"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data.userId").description("유저 고유 아이디"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.nickname").description("닉네임"),
                                fieldWithPath("data.fillPercent").description("Fill 퍼센테이지"),
                                fieldWithPath("data.job").description("직업")
                        ))
                );
    }

    private AuthResponse joinWillReturnDto(AuthRequest authRequest) {
        return AuthResponse.builder()
                .userId(1L)
                .email(authRequest.getEmail())
                .nickname(authRequest.getNickname())
                .fillPercent(0L)
                .job(Job.STUDENT)
                .build();

    }

}