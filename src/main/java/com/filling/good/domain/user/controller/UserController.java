package com.filling.good.domain.user.controller;

import com.filling.good.domain.user.dto.response.UserResponse;
import com.filling.good.domain.user.entity.User;
import com.filling.good.global.dto.response.ResultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/info")
    public ResponseEntity<Object> getUserInfo(@AuthenticationPrincipal User user) {
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("현재 로그인 된 유저 정보", UserResponse.of(user)));
    }

}
