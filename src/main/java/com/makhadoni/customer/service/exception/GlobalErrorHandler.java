package com.makhadoni.customer.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import javax.security.sasl.AuthenticationException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class GlobalErrorHandler {

    private static final Logger logger = Logger.getLogger(GlobalErrorHandler.class.getName());

    public static Mono<ServerResponse> handleNotFoundException(NotFoundException ex) {

        logger.severe("");
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", HttpStatus.NOT_FOUND.value());
        errorAttributes.put("message", ex.getMessage());

        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleAlreadyExistsException(AlreadyExistsException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", HttpStatus.CONFLICT.value());
        errorAttributes.put("message", ex.getMessage() );

        return ServerResponse.status(HttpStatus.CONFLICT)
                .bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleInvalidArgumentException(IllegalArgumentException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", HttpStatus.BAD_REQUEST.value());
        errorAttributes.put("message", "Bad parameter value supplied, please try again");

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", HttpStatus.BAD_REQUEST.value());
        errorAttributes.put("message", ex.getErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(",")));

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleAuthenticationException(AuthenticationException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", HttpStatus.UNAUTHORIZED.value());
        errorAttributes.put("message", "Invalid login details supplied, please try again or register if you dont have an account");

        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleAuthenticationException(Throwable ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", HttpStatus.UNAUTHORIZED.value());
        errorAttributes.put("message", "Invalid login details supplied, please try again or register if you dont have an account");

        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleGenericException(Exception ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        if (ex instanceof ServerWebInputException){
            errorAttributes.put("status", HttpStatus.BAD_REQUEST.value());
            errorAttributes.put("message", "Invalid request body supplied, please try again");
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .bodyValue(errorAttributes);
        } else {
            logger.severe("Error occurred :" + ex.getMessage());
            errorAttributes.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorAttributes.put("message", "something went wrong, we are working on it");
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .bodyValue(errorAttributes);
        }

    }
}
