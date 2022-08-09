package com.filling.good.domain.user.controller;

import com.filling.good.CommonTest;
import com.filling.good.domain.user.dto.request.LoginRequest;
import com.filling.good.domain.user.dto.request.SignUpRequest;
import com.filling.good.domain.user.dto.request.TokenRequest;
import com.filling.good.domain.user.dto.response.AuthUserResponse;
import com.filling.good.domain.user.exception.DuplicateUserException;
import com.filling.good.domain.user.exception.ExpiredRefreshTokenException;
import com.filling.good.domain.user.exception.PasswordErrorException;
import com.filling.good.domain.user.exception.UserNotFoundException;
import com.filling.good.domain.user.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static com.filling.good.domain.user.enumerate.AuthProvider.DEFAULT;
import static com.filling.good.domain.user.enumerate.Job.STUDENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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

    @Test
    @DisplayName("회원가입 성공")
    void join_201() throws Exception {
        //given
        SignUpRequest signUpRequest = new SignUpRequest(
                "fill@naver.com",
                "secret1234",
                "필링굿",
                "학생"
        );

        given(authService.join(any())).willReturn(authUserResponse());

        //when
        ResultActions actions = mockMvc.perform(post("/auth/join")
                .content(objectMapper.writeValueAsString(signUpRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("data").exists())
                .andDo(print())
                .andDo(document("auth_join",
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
                                fieldWithPath("data.authProvider").description("가입 경로 (DEFAULT/GOOGLE)"),
                                fieldWithPath("data.accessToken").description("액세스 토큰 (초기값 \"\")"),
                                fieldWithPath("data.refreshToken").description("리프레쉬 토큰 (초기값 \"\")")
                        ))
                );
    }

    @Test
    @DisplayName("회원가입 실패 (이미 가입된 유저)")
    void join_409() throws Exception {
        //given
        SignUpRequest signUpRequest = new SignUpRequest(
                "fill@naver.com",
                "secret1234",
                "필링굿",
                "학생"
        );

        given(authService.join(any())).willThrow(new DuplicateUserException());

        //when
        ResultActions actions = mockMvc.perform(post("/auth/join")
                .content(objectMapper.writeValueAsString(signUpRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isConflict())
                .andExpect(jsonPath("errName").value("DuplicateUserException"))
                .andDo(print())
                .andDo(document("auth_join_DuplicateUserException",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("jobValue").description("직업 (학생/리크루터/프리랜서/개발자/기타)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("errName").description("예외 이름"),
                                fieldWithPath("message").description("예외 메세지")
                        ))
                );
    }

    @Test
    @DisplayName("로그인 성공")
    void login_200() throws Exception {
        //given
        LoginRequest loginRequest = new LoginRequest(
                "fill@naver.com",
                "secret1234"
        );

        given(authService.login(any())).willReturn(authUserResponse());

        //when
        ResultActions actions = mockMvc.perform(post("/auth/login")
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(print())
                .andDo(document("auth_login",
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
                                fieldWithPath("data.accessToken").description("액세스 토큰 (유효 시간 30분)"),
                                fieldWithPath("data.refreshToken").description("리프레쉬 토큰 (유효 시간 30일)")
                        ))
                );
    }

    @Test
    @DisplayName("로그인 실패 (가입되지 않은 유저)")
    void login_404() throws Exception {
        //given
        LoginRequest loginRequest = new LoginRequest(
                "fill@naver.com",
                "secret1234"
        );

        given(authService.login(any())).willThrow(new UserNotFoundException());

        //when
        ResultActions actions = mockMvc.perform(post("/auth/login")
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errName").value("UserNotFoundException"))
                .andDo(print())
                .andDo(document("auth_login_UserNotFoundException",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("errName").description("예외 이름"),
                                fieldWithPath("message").description("예외 메세지")
                        ))
                );
    }

    @Test
    @DisplayName("로그인 실패 (비밀번호 오류)")
    void login_400() throws Exception {
        //given
        LoginRequest loginRequest = new LoginRequest(
                "fill@naver.com",
                "secret1234"
        );

        given(authService.login(any())).willThrow(new PasswordErrorException());

        //when
        ResultActions actions = mockMvc.perform(post("/auth/login")
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errName").value("PasswordErrorException"))
                .andDo(print())
                .andDo(document("auth_login_PasswordErrorException",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("errName").description("예외 이름"),
                                fieldWithPath("message").description("예외 메세지")
                        ))
                );
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissue_200() throws Exception {
        //given
        TokenRequest tokenRequest = new TokenRequest(
                "fill@naver.com",
                "MOCK_REFRESH_TOKEN"
        );

        given(authService.tokenReIssue(any())).willReturn(authUserResponse());

        //when
        ResultActions actions = mockMvc.perform(get("/auth/reissue")
                .content(objectMapper.writeValueAsString(tokenRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(print())
                .andDo(document("auth_reissue",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("refreshToken").description("리프레쉬 토큰")
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
                                fieldWithPath("data.accessToken").description("액세스 토큰 (유효 시간 30분)"),
                                fieldWithPath("data.refreshToken").description("리프레쉬 토큰 (유효 시간 30일)")
                        ))
                );
    }

    @Test
    @DisplayName("토큰 재발급 실패 (리프레쉬 토큰 만료)")
    void reissue_403() throws Exception {
        //given
        TokenRequest tokenRequest = new TokenRequest(
                "fill@naver.com",
                "MOCK_REFRESH_TOKEN"
        );

        given(authService.tokenReIssue(any())).willThrow(new ExpiredRefreshTokenException());

        //when
        ResultActions actions = mockMvc.perform(get("/auth/reissue")
                .content(objectMapper.writeValueAsString(tokenRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("errName").value("ExpiredRefreshTokenException"))
                .andDo(print())
                .andDo(document("auth_reissue_ExpiredRefreshTokenException",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("refreshToken").description("리프레쉬 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("errName").description("예외 이름"),
                                fieldWithPath("message").description("예외 메세지")
                        ))
                );
    }

    /*
    Will Return Object
    */

    private AuthUserResponse authUserResponse() {
        return AuthUserResponse.builder()
                .userId(1L)
                .email("fill@naver.com")
                .nickname("필링굿")
                .fillPercent(0L)
                .job(STUDENT)
                .authProvider(DEFAULT)
                .accessToken("")
                .refreshToken("")
                .build();

    }

}