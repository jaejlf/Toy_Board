package com.filling.good.auth.exception;

import com.filling.good.common.enumerate.HttpStatusCode;
import com.filling.good.common.exception.CustomException;

public class InvalidTokenException extends CustomException {
    public InvalidTokenException() {
        super(HttpStatusCode.FORBIDDEN, "InvalidTokenException", "잘못된 토큰입니다.");
    }
}
