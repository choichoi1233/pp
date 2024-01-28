package com.example.ppp.config;

import com.example.marsphoto.model.UnauthorizedCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UnauthorizedCustomException.class)
    public ResponseEntity<UnauthorizedCustomException> UnauthorizedCustomException(UnauthorizedCustomException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e);
    }
}