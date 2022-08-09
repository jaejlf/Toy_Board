package com.filling.good.global.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse<T> {

    private int statusCode;
    private String errName;
    private String message;

    public static <T> ErrorResponse<Object> error(int statusCode, String errName, String message) {
        return ErrorResponse.builder()
                .statusCode(statusCode)
                .errName(errName)
                .message(message)
                .build();
    }

}
