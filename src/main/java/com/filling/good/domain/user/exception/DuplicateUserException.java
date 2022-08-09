package com.filling.good.domain.user.exception;

import com.filling.good.global.error.exception.CustomException;

import static com.filling.good.global.enumerate.HttpStatusCode.CONFLICT;

public class DuplicateUserException extends CustomException {
    public DuplicateUserException() {
        super(CONFLICT, "DuplicateUserException", "이미 가입된 유저입니다.");
    }
}
