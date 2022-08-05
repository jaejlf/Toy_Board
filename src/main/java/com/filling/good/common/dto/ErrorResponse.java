package com.filling.good.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ErrorResponse<T> {

    private int statusCode;
    private String errName;
    private String message;

    @Builder
    public ErrorResponse(int statusCode, String errName, String message) {
        this.statusCode = statusCode;
        this.errName = errName;
        this.message = message;
    }

    public static <T> ErrorResponse<Object> error(int statusCode, String errName, String message) {
        return ErrorResponse.builder()
                .statusCode(statusCode)
                .errName(errName)
                .message(message)
                .build();
    }

}
