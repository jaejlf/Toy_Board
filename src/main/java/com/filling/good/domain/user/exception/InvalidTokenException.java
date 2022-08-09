package com.filling.good.domain.user.exception;

import com.filling.good.global.error.exception.CustomException;

import static com.filling.good.global.enumerate.HttpStatusCode.FORBIDDEN;

public class InvalidTokenException extends CustomException {
    public InvalidTokenException() {
        super(FORBIDDEN, "InvalidTokenException", "잘못된 토큰입니다.");
    }
}
