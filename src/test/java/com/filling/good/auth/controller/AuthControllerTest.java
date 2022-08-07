package com.filling.good.auth.controller;

import com.filling.good.auth.config.JwtTokenProvider;
import com.filling.good.auth.dto.request.LoginRequest;
import com.filling.good.auth.dto.request.SignUpRequest;
import com.filling.good.auth.service.AuthService;
import com.filling.good.common.CommonTest;
import com.filling.good.user.dto.response.UserResponse;
import com.filling.good.user.enumerate.AuthProvider;
import com.filling.good.user.enumerate.Job;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@DisplayName("Auth 컨트롤러")
class AuthControllerTest extends CommonTest {

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 회원가입_성공() throws Exception {

        //given
        SignUpRequest signUpRequest = new SignUpRequest(
                "fill@naver.com",
                "secret1234",
                "필링굿",
                "학생"
        );

        given(authService.join(any())).willReturn(userResponse());

        //when
        ResultActions actions = mockMvc.perform(post("/auth/join")
                .content(objectMapper.writeValueAsString(signUpRequest))
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
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data.userId").description("유저 고유 아이디"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.nickname").description("닉네임"),
                                fieldWithPath("data.fillPercent").description("Fill 퍼센테이지"),
                                fieldWithPath("data.job").description("직업"),
                                fieldWithPath("data.authProvider").description("가입 경로 (DEFAULT/GOOGLE)")
                        ))
                );
    }

    @Test
    void 로그인_성공() throws Exception {

        //given
        LoginRequest loginRequest = new LoginRequest(
                "fill@naver.com",
                "secret1234"
        );

        given(authService.login(any())).willReturn(userResponse());

        //when
        ResultActions actions = mockMvc.perform(post("/auth/login")
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(print())
                .andDo(document("auth-login",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data.userId").description("유저 고유 아이디"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.nickname").description("닉네임"),
                                fieldWithPath("data.fillPercent").description("Fill 퍼센테이지"),
                                fieldWithPath("data.job").description("직업"),
                                fieldWithPath("data.authProvider").description("가입 경로 (DEFAULT/GOOGLE)"),
                                fieldWithPath("data.accessToken").description("액세스 토큰 (유효 시간 30분)")
                        ))
                );

    }

    private UserResponse userResponse() {
        return UserResponse.builder()
                .userId(1L)
                .email("fill@naver.com")
                .nickname("필링굿")
                .fillPercent(0L)
                .job(Job.STUDENT)
                .authProvider(AuthProvider.DEFAULT)
                .build();

    }

}