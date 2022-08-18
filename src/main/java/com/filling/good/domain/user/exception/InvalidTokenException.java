package com.filling.good.domain.user.exception;

import com.filling.good.global.error.exception.CustomException;

import static org.springframework.http.HttpStatus.FORBIDDEN;

public class InvalidTokenException extends CustomException {
    public InvalidTokenException() {
        super(FORBIDDEN, "InvalidTokenException", "잘못된 토큰입니다.");
    }
}
