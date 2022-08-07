package com.filling.good.auth.exception;

import com.filling.good.common.enumerate.HttpStatusCode;
import com.filling.good.common.exception.CustomException;

public class PasswordErrorException extends CustomException {
    public PasswordErrorException() {
        super(HttpStatusCode.BAD_REQUEST, "PasswordErrorException", "잘못된 비밀번호 입니다.");
    }
}
