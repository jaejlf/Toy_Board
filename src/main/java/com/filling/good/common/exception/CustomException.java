package com.filling.good.common.exception;

import com.filling.good.common.dto.ErrorResponse;
import com.filling.good.common.enumerate.HttpStatusCode;
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
