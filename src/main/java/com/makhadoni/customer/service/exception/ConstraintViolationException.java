package com.makhadoni.customer.service.exception;

import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Set;

public class ConstraintViolationException extends RuntimeException{

    private final List<ObjectError> errors;
    public ConstraintViolationException(String message, List<ObjectError> errors) {
        super(message);
        this.errors = errors;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }
}
