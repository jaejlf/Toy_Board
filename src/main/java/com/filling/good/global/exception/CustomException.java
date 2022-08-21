package com.filling.good.global.exception;

import com.filling.good.global.dto.response.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorResponse<Object> errorResponse;

    protected CustomException(HttpStatus httpStatus, String errName, String message) {
        this.status = httpStatus;
        this.errorResponse = ErrorResponse.error(httpStatus.value(), errName, message);
    }

}
