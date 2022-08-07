package com.filling.good.auth.exception;

import com.filling.good.common.enumerate.HttpStatusCode;
import com.filling.good.common.exception.CustomException;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException() {
        super(HttpStatusCode.NOT_FOUND, "UserNotFoundException", "가입되지 않은 유저입니다.");
    }
}
