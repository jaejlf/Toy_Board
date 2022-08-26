package com.filling.good.domain.user.service;

import com.filling.good.domain.user.dto.request.LoginRequest;
import com.filling.good.domain.user.dto.request.ReissueRequest;
import com.filling.good.domain.user.dto.request.SignUpRequest;
import com.filling.good.domain.user.dto.response.TokenResponse;
import com.filling.good.domain.user.dto.response.UserResponse;
import com.filling.good.domain.user.entity.User;
import com.filling.good.domain.user.exception.InvalidTokenException;
import com.filling.good.domain.user.exception.LoginRequestException;
import com.filling.good.support.CommonServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.persistence.EntityExistsException;
import java.time.Duration;

import static com.filling.good.domain.user.enumerate.AuthProvider.DEFAULT;
import static com.filling.good.domain.user.enumerate.Job.STUDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth 서비스")
class AuthServiceTest extends CommonServiceTest {

    @Autowired
    private AuthService authService;

    @DisplayName("회원가입 성공")
    @Test
    void join() {
        //given
        SignUpRequest signUpRequest = new SignUpRequest(
                "newnew@new.com",
                "{{RAW_PASSWORD}}",
                "뉴뉴뉴",
                "뉴뉴뉴",
                "학생"
        );

        //when
        UserResponse result = authService.join(signUpRequest);

        //then
        UserResponse expected = UserResponse.of(new User(
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getNickname(),
                signUpRequest.getName(),
                STUDENT,
                DEFAULT
        ));

        assertAll(
                () -> assertThat(result.getEmail()).isEqualTo(expected.getEmail()),
                () -> assertThat(result.getNickname()).isEqualTo(expected.getNickname()),
                () -> assertThat(result.getName()).isEqualTo(expected.getName()),
                () -> assertThat(result.getFillPercent()).isEqualTo(expected.getFillPercent()),
                () -> assertThat(result.getJob()).isEqualTo(expected.getJob()),
                () -> assertThat(result.getAuthProvider()).isEqualTo(expected.getAuthProvider())
        );
    }

    @DisplayName("회원가입 실패 (이미 가입된 유저)")
    @Test
    void join_EntityExistsException() {
        //given
        SignUpRequest signUpRequest = new SignUpRequest(
                defaultUser.getEmail(),
                "{{RAW_PASSWORD}}",
                "기가입",
                "기가입",
                "학생"
        );

        //when & then
        assertThatThrownBy(() -> authService.join(signUpRequest))
                .isInstanceOf(EntityExistsException.class);
    }

    @DisplayName("DEFAULT 로그인 성공")
    @Test
    void defaultLogin() {
        //given
        LoginRequest loginRequest = new LoginRequest(
                defaultUser.getEmail(),
                "{{RAW_PASSWORD}}"
        );

        //when
        TokenResponse result = authService.defaultLogin(loginRequest);

        //then
        assertAll(
                () -> assertThat(result.getAccessToken()).isNotEmpty(),
                () -> assertThat(result.getRefreshToken()).isNotEmpty()
        );
    }

    @DisplayName("DEFAULT 로그인 실패 (가입되지 않은 유저)")
    @Test
    void defaultLogin_UsernameNotFoundException() {
        //given
        LoginRequest loginRequest = new LoginRequest(
                "nonono@xxx.com",
                "{{RAW_PASSWORD}}"
        );

        //when & then
        assertThatThrownBy(() -> authService.defaultLogin(loginRequest))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @DisplayName("DEFAULT 로그인 실패 (비밀번호 오류)")
    @Test
    void defaultLogin_IllegalArgumentException() {
        //given
        LoginRequest loginRequest = new LoginRequest(
                defaultUser.getEmail(),
                "{{WRONG_PASSWORD}}"
        );

        //when & then
        assertThatThrownBy(() -> authService.defaultLogin(loginRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("DEFAULT 로그인 실패 (소셜 로그인으로 가입된 유저일 경우)")
    @Test
    void defaultLogin_LoginRequestException() {
        //given
        LoginRequest loginRequest = new LoginRequest(
                googleUser.getEmail(),
                "{{RAW_PASSWORD}}"
        );

        //when & then
        assertThatThrownBy(() -> authService.defaultLogin(loginRequest))
                .isInstanceOf(LoginRequestException.class);
    }

    @DisplayName("토큰 재발급 성공")
    @Test
    void reissue() {
        //given
        ReissueRequest reissueRequest = new ReissueRequest(
                defaultUser.getEmail(),
                jwtTokenProvider.createRefreshToken(defaultUser)
        );

        //when
        TokenResponse result = authService.tokenReIssue(reissueRequest);

        //then
        assertAll(
                () -> assertThat(result.getAccessToken()).isNotEmpty(),
                () -> assertThat(result.getRefreshToken()).isNotEmpty()
        );
    }

    @DisplayName("토큰 재발급 실패 (가입되지 않은 유저)")
    @Test
    void reissue_UsernameNotFoundException() {
        //given
        ReissueRequest reissueRequest = new ReissueRequest(
                "nonono@xxx.com",
                "{{REFRESH_TOKEN}}"
        );

        //when & then
        assertThatThrownBy(() -> authService.tokenReIssue(reissueRequest))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @DisplayName("토큰 재발급 실패 (DB에 저장된 토큰과 불일치)")
    @Test
    void reissue_InvalidTokenException() {
        //before
        String expiredRefreshToken = jwtTokenProvider.createRefreshToken(defaultUser);
        redisService.setValues(defaultUser.getEmail(), expiredRefreshToken, Duration.ofMillis(1L)); //토큰 강제로 만료

        //given
        ReissueRequest reissueRequest = new ReissueRequest(
                defaultUser.getEmail(),
                expiredRefreshToken
        );

        //when & then
        assertThatThrownBy(() -> authService.tokenReIssue(reissueRequest))
                .isInstanceOf(InvalidTokenException.class);
    }

}