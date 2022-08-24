package com.filling.good.domain.user.controller;

import com.filling.good.domain.user.dto.request.LoginRequest;
import com.filling.good.domain.user.dto.request.ReissueRequest;
import com.filling.good.domain.user.dto.request.SignUpRequest;
import com.filling.good.domain.user.dto.response.TokenResponse;
import com.filling.good.domain.user.dto.response.UserResponse;
import com.filling.good.domain.user.exception.CustomJwtException;
import com.filling.good.domain.user.exception.InvalidTokenException;
import com.filling.good.domain.user.exception.LoginRequestException;
import com.filling.good.domain.user.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityExistsException;

import static com.filling.good.domain.user.enumerate.AuthProvider.DEFAULT;
import static com.filling.good.domain.user.enumerate.Job.STUDENT;
import static com.filling.good.global.exception.ErrorMessage.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.FORBIDDEN;
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
class AuthControllerTest extends CommonControllerTest {

    @MockBean
    private AuthService authService;

    @DisplayName("회원가입 성공")
    @Test
    void join_201() throws Exception {
        //given
        SignUpRequest signUpRequest = getSignUpRequest();
        given(authService.join(any())).willReturn(getUserResponse());

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
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("jobValue").description("직업 (학생/리크루터/프리랜서/개발자/기타)")
                        ),
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

    @DisplayName("회원가입 실패 (이미 가입된 유저)")
    @Test
    void join_EntityExistsException() throws Exception {
        //given
        SignUpRequest signUpRequest = getSignUpRequest();
        given(authService.join(any())).willThrow(new EntityExistsException(USER_ALREADY_EXIST.getMsg()));

        //when
        ResultActions actions = mockMvc.perform(post("/auth/join")
                .content(objectMapper.writeValueAsString(signUpRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isConflict())
                .andExpect(jsonPath("errName").value("EntityExistsException"))
                .andDo(print())
                .andDo(document("auth_join_EntityExistsException",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("jobValue").description("직업 (학생/리크루터/프리랜서/개발자/기타)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("errName").description("예외 이름"),
                                fieldWithPath("message").description("예외 메세지")
                        ))
                );
    }

    @DisplayName("DEFAULT 로그인 성공")
    @Test
    void defaultLogin_200() throws Exception {
        //given
        LoginRequest loginRequest = getLoginRequest();
        given(authService.defaultLogin(any())).willReturn(getTokenResponse());

        //when
        ResultActions actions = mockMvc.perform(post("/auth/login")
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(print())
                .andDo(document("auth_defaultLogin",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data.accessToken").description("액세스 토큰 (유효 시간 30분)"),
                                fieldWithPath("data.refreshToken").description("리프레쉬 토큰 (유효 시간 30일)")
                        ))
                );
    }

    @DisplayName("DEFAULT 로그인 실패 (가입되지 않은 유저)")
    @Test
    void defaultLogin_UsernameNotFoundException() throws Exception {
        //given
        LoginRequest loginRequest = getLoginRequest();
        given(authService.defaultLogin(any())).willThrow(new UsernameNotFoundException(USER_NOT_FOUND.getMsg()));

        //when
        ResultActions actions = mockMvc.perform(post("/auth/login")
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errName").value("UsernameNotFoundException"))
                .andDo(print())
                .andDo(document("auth_defaultLogin_UsernameNotFoundException",
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

    @DisplayName("DEFAULT 로그인 실패 (비밀번호 오류)")
    @Test
    void defaultLogin_IllegalArgumentException() throws Exception {
        //given
        LoginRequest loginRequest = getLoginRequest();
        given(authService.defaultLogin(any())).willThrow(new IllegalArgumentException(PASSWORD_ERROR.getMsg()));

        //when
        ResultActions actions = mockMvc.perform(post("/auth/login")
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errName").value("IllegalArgumentException"))
                .andDo(print())
                .andDo(document("auth_defaultLogin_IllegalArgumentException",
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

    @DisplayName("DEFAULT 로그인 실패 (소셜 로그인으로 가입된 유저일 경우)")
    @Test
    void defaultLogin_LoginRequestException() throws Exception {
        //given
        LoginRequest loginRequest = getLoginRequest();
        given(authService.defaultLogin(any())).willThrow(new LoginRequestException());

        //when
        ResultActions actions = mockMvc.perform(post("/auth/login")
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("errName").value("LoginRequestException"))
                .andDo(print())
                .andDo(document("auth_defaultLogin_LoginRequestException",
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

    @DisplayName("토큰 재발급 성공")
    @Test
    void reissue_200() throws Exception {
        //given
        ReissueRequest tokenRequest = getReissueRequest();
        given(authService.tokenReIssue(any())).willReturn(getTokenResponse());

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
                                fieldWithPath("data.accessToken").description("액세스 토큰 (유효 시간 30분)"),
                                fieldWithPath("data.refreshToken").description("리프레쉬 토큰 (유효 시간 30일)")
                        ))
                );
    }

    @DisplayName("토큰 재발급 실패 (가입되지 않은 유저)")
    @Test
    void reissue_UsernameNotFoundException() throws Exception {
        //given
        ReissueRequest tokenRequest = getReissueRequest();
        given(authService.tokenReIssue(any())).willThrow(new UsernameNotFoundException(USER_NOT_FOUND.getMsg()));

        //when
        ResultActions actions = mockMvc.perform(get("/auth/reissue")
                .content(objectMapper.writeValueAsString(tokenRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errName").value("UsernameNotFoundException"))
                .andDo(print())
                .andDo(document("auth_reissue_UsernameNotFoundException",
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

    @DisplayName("토큰 재발급 실패 (리프레쉬 토큰 만료)")
    @Test
    void reissue_CustomJwtException() throws Exception {
        //given
        ReissueRequest tokenRequest = getReissueRequest();
        given(authService.tokenReIssue(any())).willThrow(new CustomJwtException(FORBIDDEN, "리프레쉬 토큰"));

        //when
        ResultActions actions = mockMvc.perform(get("/auth/reissue")
                .content(objectMapper.writeValueAsString(tokenRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("errName").value("ExpiredJwtException"))
                .andDo(print())
                .andDo(document("auth_reissue_ExpiredJwtException",
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

    @DisplayName("토큰 재발급 실패 (DB에 저장된 토큰과 불일치)")
    @Test
    void reissue_InvalidTokenException() throws Exception {
        //given
        ReissueRequest tokenRequest = getReissueRequest();
        given(authService.tokenReIssue(any())).willThrow(new InvalidTokenException());

        //when
        ResultActions actions = mockMvc.perform(get("/auth/reissue")
                .content(objectMapper.writeValueAsString(tokenRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("errName").value("InvalidTokenException"))
                .andDo(print())
                .andDo(document("auth_reissue_InvalidTokenException",
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

    private SignUpRequest getSignUpRequest() {
        return new SignUpRequest(
                "fill@naver.com",
                "{{RAW_PASSWORD}}",
                "필링굿",
                "이름이름",
                "학생"
        );
    }

    private LoginRequest getLoginRequest() {
        return new LoginRequest(
                "fill@naver.com",
                "{{RAW_PASSWORD}}"
        );
    }

    private ReissueRequest getReissueRequest() {
        return new ReissueRequest(
                "fill@naver.com",
                "{{REFRESH_TOKEN}}"
        );
    }

    private TokenResponse getTokenResponse() {
        return TokenResponse.builder()
                .accessToken("{{ACCESS_TOKEN}}")
                .refreshToken("{{REFRESH_TOKEN}}")
                .build();
    }

    private UserResponse getUserResponse() {
        return UserResponse.builder()
                .userId(1L)
                .email("fill@naver.com")
                .nickname("필링굿")
                .fillPercent(0L)
                .job(STUDENT)
                .authProvider(DEFAULT)
                .build();
    }

}