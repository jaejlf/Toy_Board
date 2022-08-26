package com.filling.good.domain.user.controller;

import com.filling.good.domain.user.dto.request.LoginRequest;
import com.filling.good.domain.user.dto.request.SignUpRequest;
import com.filling.good.domain.user.dto.request.ReissueRequest;
import com.filling.good.domain.user.dto.response.TokenResponse;
import com.filling.good.domain.user.dto.response.UserResponse;
import com.filling.good.domain.user.service.AuthService;
import com.filling.good.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.filling.good.domain.user.enumerate.AuthProvider.DEFAULT;
import static com.filling.good.domain.user.enumerate.AuthProvider.GOOGLE;
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
                .body(ResultResponse.create("회원 가입", user));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> defaultLogin(@RequestBody LoginRequest loginRequest) {
        TokenResponse user = authService.defaultLogin(loginRequest);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok(DEFAULT + " 로그인", user));
    }

    // {FILLing-GOOD-URL}/oauth2/authorization/google 요청 후 리다이렉트
    @GetMapping("/login/google")
    public ResponseEntity<Object> googleLogin(@RequestParam String accessToken,
                                              @RequestParam String refreshToken) {
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok(GOOGLE + " 로그인", TokenResponse.of(accessToken, refreshToken)));
    }

    @GetMapping("/reissue")
    public ResponseEntity<Object> tokenReIssue(@RequestBody ReissueRequest reissueRequest) {
        TokenResponse tokenResponse = authService.tokenReIssue(reissueRequest);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("토큰 재발급", tokenResponse));
    }

}