package com.makhadoni.customer.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GlobalErrorHandler {

    public static Mono<ServerResponse> handleNotFoundException(NotFoundException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", HttpStatus.NOT_FOUND.value());
        errorAttributes.put("error", "Not Found");
        errorAttributes.put("message", ex.getMessage());

        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleAlreadyExistsException(UserAlreadyExistsException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", HttpStatus.CONFLICT.value());
        errorAttributes.put("error", "already exist");
        errorAttributes.put("message", ex.getMessage());

        return ServerResponse.status(HttpStatus.CONFLICT)
                .bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleContraintViolation(ConstraintViolationException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", HttpStatus.BAD_REQUEST.value());
        errorAttributes.put("error", ex.getErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(",")));
        errorAttributes.put("message", ex.getMessage());

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleGenericException(Exception ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorAttributes.put("error", ex.getMessage());
        errorAttributes.put("message", "something went wrong, we are working on it");

        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue(errorAttributes);
    }
}
