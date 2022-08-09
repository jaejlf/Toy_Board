package com.filling.good.domain.user.controller;

import com.filling.good.global.config.JwtTokenProvider;
import com.filling.good.domain.user.dto.request.LoginRequest;
import com.filling.good.domain.user.dto.request.SignUpRequest;
import com.filling.good.domain.user.dto.response.LoginResponse;
import com.filling.good.domain.user.service.AuthService;
import com.filling.good.global.dto.response.ResultResponse;
import com.filling.good.domain.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

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
                .status(CREATED)
                .body(ResultResponse.create("회원 가입 성공", user));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        UserResponse user = authService.login(loginRequest);
        String token = jwtTokenProvider.createAccessToken(user.getEmail());
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.success("로그인 성공", LoginResponse.of(user, token)));
    }

}
