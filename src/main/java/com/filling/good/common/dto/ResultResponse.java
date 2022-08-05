package com.filling.good.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ResultResponse<T> {

    private static final int OK = 200;
    public static final int CREATED = 201;

    private int statusCode;
    private String message;
    private T data;

    @Builder
    public ResultResponse(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static <T> ResultResponse<Object> success(String msg, T data) {
        return ResultResponse.builder()
                .statusCode(OK)
                .message(msg)
                .data(data)
                .build();
    }


    public static <T> ResultResponse<Object> create(String message, T data) {
        return ResultResponse.builder()
                .statusCode(CREATED)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResultResponse<Object> update(String msg, T data) {
        return ResultResponse.builder()
                .statusCode(OK)
                .message(msg)
                .data(data)
                .build();
    }

}