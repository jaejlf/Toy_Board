package com.filling.good.domain.user.exception;

import com.filling.good.global.error.exception.CustomException;

import static com.filling.good.global.enumerate.HttpStatusCode.BAD_REQUEST;

public class PasswordErrorException extends CustomException {
    public PasswordErrorException() {
        super(BAD_REQUEST, "PasswordErrorException", "잘못된 비밀번호 입니다.");
    }
}
