package com.filling.good.global.error;

import com.filling.good.global.dto.response.ErrorResponse;
import com.filling.good.global.error.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityExistsException;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private void errorLogging(Exception e) {
        log.error("[" + e.getClass().getSimpleName() + "] " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> defaultExceptionHandler(Exception e) {
        errorLogging(e);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.error(INTERNAL_SERVER_ERROR.value(), e.getClass().getSimpleName(), e.getMessage()));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> customExceptionHandler(CustomException e) {
        errorLogging(e);
        return ResponseEntity
                .status(e.getStatus())
                .body(e.getErrorResponse());
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Object> entityExistsExceptionHandler(EntityExistsException e) {
        errorLogging(e);
        return ResponseEntity
                .status(CONFLICT)
                .body(ErrorResponse.error(CONFLICT.value(), e.getClass().getSimpleName(), e.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> usernameNotFoundExceptionHandler(UsernameNotFoundException e) {
        errorLogging(e);
        return ResponseEntity
                .status(NOT_FOUND)
                .body(ErrorResponse.error(NOT_FOUND.value(), e.getClass().getSimpleName(), e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        errorLogging(e);
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ErrorResponse.error(BAD_REQUEST.value(), e.getClass().getSimpleName(), e.getMessage()));
    }

}
