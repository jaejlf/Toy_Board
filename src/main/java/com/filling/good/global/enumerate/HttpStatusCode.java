package com.filling.good.global.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum HttpStatusCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400),
    UnAuthorized(HttpStatus.UNAUTHORIZED, 401),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403),
    NOT_FOUND(HttpStatus.NOT_FOUND, 404),
    CONFLICT(HttpStatus.CONFLICT, 409),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500);

    private final HttpStatus httpStatus;
    private final int statusCode;

}
