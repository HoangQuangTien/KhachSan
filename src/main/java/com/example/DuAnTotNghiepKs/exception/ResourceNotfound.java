package com.example.DuAnTotNghiepKs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotfound extends RuntimeException{

    public ResourceNotfound(String message) {
        super(message);
    }
}
