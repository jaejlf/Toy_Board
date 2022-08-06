package com.filling.good.auth.exception;

import com.filling.good.common.enumerate.HttpStatusCode;
import com.filling.good.common.exception.CustomException;

public class DuplicateUserException extends CustomException {
    public DuplicateUserException() {
        super(HttpStatusCode.CONFLICT, "DuplicateUserException", "이미 가입된 유저입니다.");
    }
}
