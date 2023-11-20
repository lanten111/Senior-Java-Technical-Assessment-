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

    private static final String STATUS = "status";
    private static final String MESSAGE = "message";

    public static Mono<ServerResponse> handleNotFoundException(NotFoundException ex) {

        logger.severe("");
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put(STATUS, HttpStatus.NOT_FOUND.value());
        errorAttributes.put(MESSAGE, ex.getMessage());

        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleAlreadyExistsException(AlreadyExistsException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put(STATUS, HttpStatus.CONFLICT.value());
        errorAttributes.put(MESSAGE, ex.getMessage() );

        return ServerResponse.status(HttpStatus.CONFLICT)
                .bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleInvalidArgumentException(IllegalArgumentException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put(STATUS, HttpStatus.BAD_REQUEST.value());
        errorAttributes.put(MESSAGE, "Bad parameter value supplied, please try again");

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put(STATUS, HttpStatus.BAD_REQUEST.value());
        errorAttributes.put(MESSAGE, ex.getErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(",")));

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleAuthenticationException(AuthenticationException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put(STATUS, HttpStatus.UNAUTHORIZED.value());
        errorAttributes.put(MESSAGE, ex.getMessage());

        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleGenericException(Exception ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        if (ex instanceof ServerWebInputException){
            errorAttributes.put(STATUS, HttpStatus.BAD_REQUEST.value());
            errorAttributes.put(MESSAGE, "Invalid request body supplied, please try again");
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .bodyValue(errorAttributes);
        } else {
            logger.severe("Error occurred :" + ex.getMessage());
            errorAttributes.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorAttributes.put(MESSAGE, "something went wrong, we are working on it");
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .bodyValue(errorAttributes);
        }

    }
}
