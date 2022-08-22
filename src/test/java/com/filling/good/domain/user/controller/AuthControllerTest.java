package com.filling.good.domain.user.controller;

import com.filling.good.CommonTest;
import com.filling.good.domain.user.dto.request.LoginRequest;
import com.filling.good.domain.user.dto.request.ReissueRequest;
import com.filling.good.domain.user.dto.request.SignUpRequest;
import com.filling.good.domain.user.dto.response.TokenResponse;
import com.filling.good.domain.user.dto.response.UserResponse;
import com.filling.good.domain.user.exception.CustomJwtException;
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
                "이름이름",
                "학생"
        );

        given(authService.join(any())).willReturn(userResponse());

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

    @Test
    @DisplayName("회원가입 실패 (이미 가입된 유저)")
    void join_409() throws Exception {
        //given
        SignUpRequest signUpRequest = new SignUpRequest(
                "fill@naver.com",
                "secret1234",
                "필링굿",
                "이름이름",
                "학생"
        );

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
                .andDo(document("auth_join_DuplicateUserException",
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

    @Test
    @DisplayName("DEFAULT 로그인 성공")
    void defaultLogin_200() throws Exception {
        //given
        LoginRequest loginRequest = new LoginRequest(
                "fill@naver.com",
                "secret1234"
        );

        given(authService.defaultLogin(any())).willReturn(tokenResponse());

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

    @Test
    @DisplayName("DEFAULT 로그인 실패 (가입되지 않은 유저)")
    void defaultLogin_404() throws Exception {
        //given
        LoginRequest loginRequest = new LoginRequest(
                "fill@naver.com",
                "secret1234"
        );

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
                .andDo(document("auth_defaultLogin_UserNotFoundException",
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
    @DisplayName("DEFAULT 로그인 실패 (비밀번호 오류)")
    void defaultLogin_400() throws Exception {
        //given
        LoginRequest loginRequest = new LoginRequest(
                "fill@naver.com",
                "secret1234"
        );

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
                .andDo(document("auth_defaultLogin_PasswordErrorException",
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
    @DisplayName("DEFAULT 로그인 실패 (소셜 로그인으로 가입된 유저일 경우)")
    void defaultLogin_403() throws Exception {
        //given
        LoginRequest loginRequest = new LoginRequest(
                "fill@naver.com",
                "secret1234"
        );

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

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissue_200() throws Exception {
        //given
        ReissueRequest tokenRequest = new ReissueRequest(
                "fill@naver.com",
                "MOCK_REFRESH_TOKEN"
        );

        given(authService.tokenReIssue(any())).willReturn(tokenResponse());

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

    @Test
    @DisplayName("토큰 재발급 실패 (리프레쉬 토큰 만료)")
    void reissue_403() throws Exception {
        //given
        ReissueRequest tokenRequest = new ReissueRequest(
                "fill@naver.com",
                "MOCK_REFRESH_TOKEN"
        );

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

    private TokenResponse tokenResponse() {
        return TokenResponse.builder()
                .accessToken("ACCESS_TOKEN")
                .refreshToken("REFRESH_TOKEN")
                .build();

    }

    private UserResponse userResponse() {
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