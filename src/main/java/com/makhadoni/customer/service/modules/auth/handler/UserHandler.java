package com.makhadoni.customer.service.modules.auth.handler;

import com.makhadoni.customer.service.exception.AlreadyExistsException;
import com.makhadoni.customer.service.exception.ConstraintViolationException;
import com.makhadoni.customer.service.exception.GlobalErrorHandler;
import com.makhadoni.customer.service.modules.auth.dto.UserDto;
import com.makhadoni.customer.service.security.TokenService;
import com.makhadoni.customer.service.modules.auth.service.UserServiceImpl;
import com.makhadoni.customer.service.validator.UserValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.security.sasl.AuthenticationException;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class UserHandler {

    private final UserServiceImpl service;
    private final UserValidator validator;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    private static final Logger logger = Logger.getLogger(UserHandler.class.getName());

    public static final String UNOTHORIXED_MESSAGE = "Invalid login details supplied, please try again or register if you don't have an account";

    public UserHandler(UserServiceImpl service, UserValidator validator,
                       PasswordEncoder passwordEncoder, TokenService TokenService) {
        this.service = service;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = TokenService;
    }

    public Mono<ServerResponse> signIn(ServerRequest request){
        return request.bodyToMono(UserDto.class)
                .flatMap(this::validateUserObject)
                .flatMap(suppliedUser -> service.getUser(suppliedUser.getUsername())
                        .filter(foundUser -> passwordEncoder.matches(suppliedUser.getPassword(), foundUser.getPassword())))
                .flatMap(userDetails -> ServerResponse.ok().contentType(APPLICATION_JSON)
                        .body(BodyInserters.fromValue(tokenService.getToken(userDetails.getUsername())))
                        .doOnNext(r -> logger.log(Level.INFO, "user: "+userDetails.getUsername()+ "just logged in")))
                .switchIfEmpty(Mono.error(new AuthenticationException(UNOTHORIXED_MESSAGE)))
                .onErrorResume(AuthenticationException.class, GlobalErrorHandler::handleAuthenticationException)
                .onErrorResume(ConstraintViolationException.class, GlobalErrorHandler::handleConstraintViolation)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }



    public Mono<ServerResponse> createUser(ServerRequest request){
        return request.bodyToMono(UserDto.class)
                .flatMap(this::validateUserObject)
                .map(userDto -> {
                    userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
                    return userDto;
                })
                .flatMap(service::createUser)
                .flatMap(savedCustomer -> ServerResponse.ok().contentType(APPLICATION_JSON).body(BodyInserters.fromValue(savedCustomer)))
                .onErrorResume(ConstraintViolationException.class, GlobalErrorHandler::handleConstraintViolation)
                .onErrorResume(AlreadyExistsException.class, GlobalErrorHandler::handleAlreadyExistsException)
                .onErrorResume(IllegalArgumentException.class, GlobalErrorHandler::handleInvalidArgumentException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }


    private Mono<UserDto> validateUserObject(UserDto user){
        Errors errors = new BeanPropertyBindingResult(user, "userDto");
        validator.validate(user, errors);

        if (errors.hasErrors()) {
            throw new ConstraintViolationException("validation failed", errors.getAllErrors());
        } else {
            return Mono.just(user);
        }
    }

}
