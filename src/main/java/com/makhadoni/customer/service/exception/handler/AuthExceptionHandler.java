package com.makhadoni.customer.service.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makhadoni.customer.service.exception.AuthenticationException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class AuthExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger logger = Logger.getLogger(ErrorWebExceptionHandler.class.getName());

    private final ObjectMapper objectMapper;

    public AuthExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        Map<String, String> appError = new HashMap<>();

        if (ex instanceof AuthenticationException) {
            appError.put(GlobalExceptionHandler.CODE, String.valueOf(HttpStatus.UNAUTHORIZED.value()));
            appError.put(GlobalExceptionHandler.MESSAGE, ex.getMessage());
            logger.severe("AuthenticationException Error occurred :" + ex.getMessage());
        } else if (ex instanceof SignatureException) {
            appError.put(GlobalExceptionHandler.CODE, String.valueOf(HttpStatus.UNAUTHORIZED.value()));
            appError.put(GlobalExceptionHandler.MESSAGE, "Invalid or Missing token provided, please try again");
            logger.severe("AuthenticationException Error occurred :" + ex.getMessage());
        } else {
            appError.put(GlobalExceptionHandler.CODE, String.valueOf(HttpStatus.UNAUTHORIZED.value()));
            appError.put(GlobalExceptionHandler.MESSAGE, "Authentication failed");
            logger.severe("Unknown Error occurred :" + ex.getMessage());
        }

        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(status);
        return write(exchange.getResponse(), appError);
    }

  public <T> Mono<Void> write(ServerHttpResponse httpResponse, T object) {
    return httpResponse.writeWith(Mono.fromSupplier(() -> {
        DataBufferFactory bufferFactory = httpResponse.bufferFactory();
        try {
            return bufferFactory.wrap(objectMapper.writeValueAsBytes(object));
        } catch (Exception ex) {
            return bufferFactory.wrap(new byte[0]);
        }
    }));
  }
}