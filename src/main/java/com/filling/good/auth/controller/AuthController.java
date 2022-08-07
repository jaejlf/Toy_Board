package com.filling.good.auth.controller;

import com.filling.good.auth.config.JwtTokenProvider;
import com.filling.good.auth.dto.request.LoginRequest;
import com.filling.good.auth.dto.request.SignUpRequest;
import com.filling.good.auth.dto.response.LoginResponse;
import com.filling.good.auth.service.AuthService;
import com.filling.good.common.dto.ResultResponse;
import com.filling.good.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/join")
    public ResponseEntity<Object> join(@RequestBody SignUpRequest signUpRequest) {
        UserResponse user = authService.join(signUpRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResultResponse.create("회원 가입 성공", user));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        UserResponse user = authService.login(loginRequest);
        String token = jwtTokenProvider.createAccessToken(user.getEmail());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultResponse.success("로그인 성공", LoginResponse.of(user, token)));
    }

}
