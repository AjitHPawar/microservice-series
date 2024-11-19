package com.microservice.accounts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RecordAllreadyExistException extends RuntimeException {
    public RecordAllreadyExistException(String message) {
        super(message);
    }
}
