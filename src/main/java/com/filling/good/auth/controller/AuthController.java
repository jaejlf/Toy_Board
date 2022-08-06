package com.filling.good.auth.controller;

import com.filling.good.auth.dto.request.AuthRequest;
import com.filling.good.auth.dto.response.AuthResponse;
import com.filling.good.auth.service.AuthService;
import com.filling.good.common.dto.ResultResponse;
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

    @PostMapping("/join")
    public ResponseEntity<Object> join(@RequestBody AuthRequest authRequest) {
        AuthResponse user = authService.save(authRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResultResponse.create("회원 가입 완료", user));
    }

}
