package com.filling.good.domain.user.controller;

import com.filling.good.domain.user.dto.request.LoginRequest;
import com.filling.good.domain.user.dto.request.SignUpRequest;
import com.filling.good.domain.user.dto.request.TokenRequest;
import com.filling.good.domain.user.dto.response.AuthUserResponse;
import com.filling.good.domain.user.dto.response.UserResponse;
import com.filling.good.domain.user.service.AuthService;
import com.filling.good.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<Object> join(@RequestBody SignUpRequest signUpRequest) {
        UserResponse user = authService.join(signUpRequest);
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("회원 가입 성공", user));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        AuthUserResponse user = authService.login(loginRequest);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("로그인 성공", user));
    }

    @GetMapping("/reissue")
    public ResponseEntity<Object> tokenReIssue(@RequestBody TokenRequest tokenRequest) {
        AuthUserResponse user = authService.tokenReIssue(tokenRequest);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("토큰 재발급 완료", user));
    }

}