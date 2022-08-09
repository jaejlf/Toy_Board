package com.filling.good.domain.user.exception;

import com.filling.good.global.error.exception.CustomException;

import static com.filling.good.global.enumerate.HttpStatusCode.FORBIDDEN;

public class ExpiredRefreshTokenException extends CustomException {
    public ExpiredRefreshTokenException() {
        super(FORBIDDEN, "ExpiredRefreshTokenException", "만료된 리프레쉬 토큰입니다.");
    }
}
