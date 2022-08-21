package com.filling.good.domain.user.exception;

import com.filling.good.global.exception.CustomException;

import static org.springframework.http.HttpStatus.FORBIDDEN;

public class LoginRequestException extends CustomException {
    public LoginRequestException() {
        super(FORBIDDEN, "LoginRequestException", "로그인 요청 링크가 잘못되었습니다.");
    }
}
