package com.filling.good.global.error;

import com.filling.good.global.dto.response.ErrorResponse;
import com.filling.good.global.error.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> defaultExceptionHandler(Exception e) {
        log.error("[" + e.getClass().getSimpleName() + "] " + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.error(500, e.getClass().getSimpleName(), e.getMessage()));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> customExceptionHandler(CustomException e) {
        log.error("[" + e.getErrorResponse().getErrName() + "] " + e.getErrorResponse().getMessage());
        return ResponseEntity.status(e.getStatus()).body(e.getErrorResponse());
    }

}
