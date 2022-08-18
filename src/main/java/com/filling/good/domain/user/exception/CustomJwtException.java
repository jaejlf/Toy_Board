package com.filling.good.domain.user.exception;

import com.filling.good.global.error.exception.CustomException;
import org.springframework.http.HttpStatus;

public class CustomJwtException extends CustomException {
    public CustomJwtException(HttpStatus httpStatus, String tokenName) {
        super(httpStatus, "ExpiredJwtException", "만료된 " + tokenName + "입니다.");
    }
}