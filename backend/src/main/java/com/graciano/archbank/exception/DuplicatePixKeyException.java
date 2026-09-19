package com.graciano.archbank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatePixKeyException extends RuntimeException{
    public DuplicatePixKeyException(String message){
        super(message);
    }
}
