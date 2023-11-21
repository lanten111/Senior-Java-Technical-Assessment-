package com.makhadoni.customer.service.exception.handler;

import com.fasterxml.jackson.databind.util.ExceptionUtil;
import com.makhadoni.customer.service.exception.AlreadyExistsException;
import com.makhadoni.customer.service.exception.AuthenticationException;
import com.makhadoni.customer.service.exception.ConstraintViolationException;
import com.makhadoni.customer.service.exception.NotFoundException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    public static final String CODE = "code";
    public static final String MESSAGE = "message";

    public static Mono<ServerResponse> handleNotFoundException(NotFoundException ex) {

        logger.severe("");
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put(CODE, HttpStatus.NOT_FOUND.value());
        errorAttributes.put(MESSAGE, ex.getMessage());
        logger.info("NotFoundException Error occurred :" + ex.getMessage());
        logger.severe("NotFoundException Error occurred :" + ExceptionUtils.getStackTrace(ex));
        return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleAlreadyExistsException(AlreadyExistsException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put(CODE, HttpStatus.CONFLICT.value());
        errorAttributes.put(MESSAGE, ex.getMessage() );
        logger.info("AlreadyExistsException Error occurred :" + ex.getMessage());
        logger.severe("AlreadyExistsException Error occurred :" + ExceptionUtils.getStackTrace(ex));
        return ServerResponse.status(HttpStatus.CONFLICT).bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleInvalidArgumentException(IllegalArgumentException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put(CODE, HttpStatus.BAD_REQUEST.value());
        errorAttributes.put(MESSAGE, "IllegalArgumentExceptionBad parameter value supplied, please try again");
        logger.info("IllegalArgumentException Error occurred :" + ex.getMessage());
        logger.severe("IllegalArgumentException Error occurred :" + ExceptionUtils.getStackTrace(ex));
        return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put(CODE, HttpStatus.BAD_REQUEST.value());
        errorAttributes.put(MESSAGE, ex.getErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(",")));
        logger.info("ConstraintViolationException Error occurred :" + ex.getMessage());
        logger.severe("ConstraintViolationException Error occurred :" + ExceptionUtils.getStackTrace(ex));
        return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleAuthenticationException(AuthenticationException ex) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put(CODE, HttpStatus.UNAUTHORIZED.value());
        errorAttributes.put(MESSAGE, ex.getMessage());
        logger.warning("AuthenticationException Error occurred :" + ex.getMessage());
        logger.severe("AuthenticationException Error occurred :" + ExceptionUtils.getStackTrace(ex));
        return ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue(errorAttributes);
    }

    public static Mono<ServerResponse> handleGenericException(Exception ex) {

        Map<String, Object> errorAttributes = new HashMap<>();
        if (ex instanceof ServerWebInputException){
            logger.severe("ServerWebInputException Error occurred :" + ExceptionUtils.getStackTrace(ex));
            errorAttributes.put(CODE, HttpStatus.BAD_REQUEST.value());
            errorAttributes.put(MESSAGE, "Invalid request body supplied, please try again");
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .bodyValue(errorAttributes);
        } else {
            logger.severe("General Exception Error occurred :" + ExceptionUtils.getStackTrace(ex));
            errorAttributes.put(CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorAttributes.put(MESSAGE, "something went wrong, we are working on it");
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .bodyValue(errorAttributes);
        }

    }
}
