package com.filling.good.common.exception;

import com.filling.good.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //default 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> defaultExceptionHandler(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.error(500, e.getClass().getSimpleName(), e.getMessage()));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> customExceptionHandler(CustomException e) {
        return ResponseEntity.status(e.getStatus()).body(e.getErrorResponse());
    }
}
