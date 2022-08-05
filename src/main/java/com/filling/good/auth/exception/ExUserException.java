package com.filling.good.auth.exception;

import com.filling.good.common.enumerate.HttpStatusCode;
import com.filling.good.common.exception.CustomException;

public class ExUserException extends CustomException {
    public ExUserException(){
        super(HttpStatusCode.CONFLICT, "ExUserException", "이미 존재하는 사용자입니다.");
    }
}
