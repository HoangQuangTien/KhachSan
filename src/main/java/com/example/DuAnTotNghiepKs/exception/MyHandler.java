package com.example.DuAnTotNghiepKs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MyHandler {

    @ExceptionHandler
    public ResponseEntity<ErrolResponse> globalException(Exception ex) {
        ErrolResponse ers = new ErrolResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ers);
    }

    @ExceptionHandler
    public ResponseEntity<ErrolResponse> batLoi2(ResourceNotfound ex) {
        ErrolResponse ers = new ErrolResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ers);
    }
}
