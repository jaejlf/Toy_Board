package com.filling.good.domain.user.exception;

import com.filling.good.global.exception.CustomException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class CustomJwtException extends CustomException {
    public CustomJwtException() {
        super(UNAUTHORIZED, "ExpiredJwtException", "만료된 액세스 토큰입니다.");
    }
}