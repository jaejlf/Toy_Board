package com.filling.good.domain.user.service;

import com.filling.good.domain.user.dto.request.LoginRequest;
import com.filling.good.domain.user.dto.request.ReissueRequest;
import com.filling.good.domain.user.dto.request.SignUpRequest;
import com.filling.good.domain.user.dto.response.TokenResponse;
import com.filling.good.domain.user.dto.response.UserResponse;
import com.filling.good.domain.user.entity.User;
import com.filling.good.domain.user.exception.CustomJwtException;
import com.filling.good.domain.user.exception.InvalidTokenException;
import com.filling.good.domain.user.exception.LoginRequestException;
import com.filling.good.domain.user.repository.UserRepository;
import com.filling.good.global.service.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;

import static com.filling.good.domain.user.enumerate.AuthProvider.DEFAULT;
import static com.filling.good.domain.user.enumerate.AuthProvider.GOOGLE;
import static com.filling.good.domain.user.enumerate.Job.STUDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@DisplayName("Auth 서비스")
class AuthServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RedisService redisService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @DisplayName("회원가입 성공")
    @Test
    void join() {
        //given
        SignUpRequest signUpRequest = getSignUpRequest();

        //when
        UserResponse result = authService.join(signUpRequest);

        //then
        UserResponse expected = UserResponse.of(getDefaultUser());

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
        userRepository.save(getDefaultUser());
        SignUpRequest signUpRequest = getSignUpRequest();

        //when & then
        assertThatThrownBy(() -> authService.join(signUpRequest))
                .isInstanceOf(EntityExistsException.class);
    }

    @DisplayName("DEFAULT 로그인 성공")
    @Test
    void defaultLogin() {
        //given
        userRepository.save(getDefaultUser());
        LoginRequest loginRequest = getLoginRequest();

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
        LoginRequest loginRequest = getLoginRequest();

        //when & then
        assertThatThrownBy(() -> authService.defaultLogin(loginRequest))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @DisplayName("DEFAULT 로그인 실패 (비밀번호 오류)")
    @Test
    void defaultLogin_IllegalArgumentException() {
        //given
        userRepository.save(getDefaultUser());
        LoginRequest loginRequest = getLoginRequestWithWrongPW();

        //when & then
        assertThatThrownBy(() -> authService.defaultLogin(loginRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("DEFAULT 로그인 실패 (소셜 로그인으로 가입된 유저일 경우)")
    @Test
    void defaultLogin_LoginRequestException() {
        //given
        userRepository.save(getGoogleUser());
        LoginRequest loginRequest = getLoginRequest();

        //when & then
        assertThatThrownBy(() -> authService.defaultLogin(loginRequest))
                .isInstanceOf(LoginRequestException.class);
    }

    @DisplayName("토큰 재발급 성공")
    @Test
    void reissue() {
        //given
        userRepository.save(getReissueUser());
        ReissueRequest reissueRequest = getReissueRequest();

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
        ReissueRequest reissueRequest = getReissueRequest();

        //when & then
        assertThatThrownBy(() -> authService.tokenReIssue(reissueRequest))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @DisplayName("토큰 재발급 실패 (리프레쉬 토큰 만료)")
    @Test
    void reissue_CustomJwtException() {
        //given
        userRepository.save(getReissueUser());
        ReissueRequest reissueRequest = getReissueRequestWithExpiredToken();

        //when & then
        assertThatThrownBy(() -> authService.tokenReIssue(reissueRequest))
                .isInstanceOf(CustomJwtException.class);
    }

    @DisplayName("토큰 재발급 실패 (DB에 저장된 토큰과 불일치)")
    @Test
    void reissue_InvalidTokenException() {
        //given
        userRepository.save(getReissueUser());
        ReissueRequest reissueRequest = getReissueRequestWithWrongToken();

        //when & then
        assertThatThrownBy(() -> authService.tokenReIssue(reissueRequest))
                .isInstanceOf(InvalidTokenException.class);
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

    private User getDefaultUser() {
        return new User(
                "fill@naver.com",
                passwordEncoder.encode("{{RAW_PASSWORD}}"),
                "필링굿",
                "이름이름",
                STUDENT,
                DEFAULT
        );
    }

    private User getGoogleUser() {
        return new User(
                "fill@naver.com",
                passwordEncoder.encode("{{RAW_PASSWORD}}"),
                "필링굿",
                "이름이름",
                STUDENT,
                GOOGLE
        );
    }

    private User getReissueUser() {
        return new User(
                "reissue@test.com",
                passwordEncoder.encode("{{RAW_PASSWORD}}"),
                "토큰토큰",
                "테스트유저",
                STUDENT,
                DEFAULT
        );
    }

    private LoginRequest getLoginRequest() {
        return new LoginRequest(
                "fill@naver.com",
                "{{RAW_PASSWORD}}"
        );
    }

    private LoginRequest getLoginRequestWithWrongPW() {
        return new LoginRequest(
                "fill@naver.com",
                "{{WRONG_PASSWORD}}"
        );
    }

    private ReissueRequest getReissueRequest() {
        return new ReissueRequest(
                "reissue@test.com",
                "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmaWxsQG5hdmVyLmNvbSIsInByb3ZpZGVyIjoiREVGQVVMVCIsImlhdCI6MTY2MTMyNzczNCwiZXhwIjoxNjkyODg1MzM0fQ.4w_0PQwkV1dUEfUvr8hagUvjJ0ZMN90NEC_qi2MoIow"
        );
    }

    private ReissueRequest getReissueRequestWithExpiredToken() {
        return new ReissueRequest(
                "reissue@test.com",
                "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmaWxsQG5hdmVyLmNvbSIsInByb3ZpZGVyIjoiREVGQVVMVCIsImlhdCI6MTY2MTMyODg4OCwiZXhwIjoxNjYxMzI4ODg4fQ.sihMMImX9mb2sCs_L4kvD6BE7UuRKBhouHbSrYMmKQA"
        );
    }

    private ReissueRequest getReissueRequestWithWrongToken() {
        return new ReissueRequest(
                "reissue@test.com",
                "{{WRONG_REFRESH_TOKEN}}"
        );
    }

}