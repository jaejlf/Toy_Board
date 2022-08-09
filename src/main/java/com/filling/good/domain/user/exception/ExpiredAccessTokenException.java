package com.filling.good.domain.user.exception;

import com.filling.good.global.error.exception.CustomException;

import static com.filling.good.global.enumerate.HttpStatusCode.UnAuthorized;

public class ExpiredAccessTokenException extends CustomException {
    public ExpiredAccessTokenException() {
        super(UnAuthorized, "ExpiredTokenException", "만료된 액세스 토큰입니다.");
    }
}
