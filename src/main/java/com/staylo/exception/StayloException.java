package com.staylo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StayloException extends RuntimeException {
    public StayloException(String message) {
        super(message);
    }
}
