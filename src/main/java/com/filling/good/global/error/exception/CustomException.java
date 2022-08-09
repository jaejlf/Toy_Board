package com.filling.good.global.error.exception;

import com.filling.good.global.dto.response.ErrorResponse;
import com.filling.good.global.enumerate.HttpStatusCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorResponse<Object> errorResponse;

    protected CustomException(HttpStatusCode httpStatusCode, String errName, String message) {
        this.status = httpStatusCode.getHttpStatus();
        this.errorResponse = ErrorResponse.error(httpStatusCode.getStatusCode(), errName, message);
    }

}
