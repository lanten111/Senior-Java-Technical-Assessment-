package com.makhadoni.customer.service.exception;

public class MissingParameterException extends RuntimeException{

    public MissingParameterException(String message) {
        super(message);
    }
}
