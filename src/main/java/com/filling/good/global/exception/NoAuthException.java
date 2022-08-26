package com.filling.good.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class NoAuthException extends CustomException {
    public NoAuthException() {
        super(BAD_REQUEST, "NoAuthException", "권한이 없는 유저입니다.");
    }
}
