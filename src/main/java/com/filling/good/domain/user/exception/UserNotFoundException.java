package com.filling.good.domain.user.exception;

import com.filling.good.global.error.exception.CustomException;

import static com.filling.good.global.enumerate.HttpStatusCode.NOT_FOUND;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException() {
        super(NOT_FOUND, "UserNotFoundException", "가입되지 않은 유저입니다.");
    }
}
