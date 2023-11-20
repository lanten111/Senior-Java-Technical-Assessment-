package com.makhadoni.customer.service.modules.customer.handler;

import com.makhadoni.customer.service.modules.customer.dto.CustomerDto;
import com.makhadoni.customer.service.exception.*;
import com.makhadoni.customer.service.modules.customer.service.CustomerService;
import com.makhadoni.customer.service.validator.CustomerValidator;
import com.makhadoni.customer.service.validator.ParameterValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.security.sasl.AuthenticationException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class CustomerHandler {

    private final CustomerService customerService;

    private final CustomerValidator validator;

    private static final String CUSTOMER_ID = "customerId";

    public CustomerHandler(CustomerService customerService, CustomerValidator validator) {
        this.customerService = customerService;
        this.validator = validator;
    }

    public Mono<ServerResponse> createCustomer(ServerRequest request){
        return request.bodyToMono(CustomerDto.class)
                .flatMap(this::validateCustomerObject)
                .flatMap(customerService::createCustomer)
                .flatMap(savedCustomer -> ServerResponse.ok().contentType(APPLICATION_JSON).body(BodyInserters.fromValue(savedCustomer)))
                .onErrorResume(ConstraintViolationException.class, GlobalErrorHandler::handleConstraintViolation)
                .onErrorResume(AlreadyExistsException.class, GlobalErrorHandler::handleAlreadyExistsException)
                .onErrorResume(IllegalArgumentException.class, GlobalErrorHandler::handleInvalidArgumentException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    public Mono<ServerResponse> updateCustomer(ServerRequest request){
        return request.bodyToMono(CustomerDto.class)
                .flatMap(this::validateCustomerObject)
                .flatMap(customerService::updateCustomer)
                .flatMap(savedCustomer -> ServerResponse.ok().contentType(APPLICATION_JSON).body(BodyInserters.fromValue(savedCustomer)))
                .onErrorResume(ConstraintViolationException.class, GlobalErrorHandler::handleConstraintViolation)
                .onErrorResume(AlreadyExistsException.class, GlobalErrorHandler::handleAlreadyExistsException)
                .onErrorResume(IllegalArgumentException.class, GlobalErrorHandler::handleInvalidArgumentException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    public Mono<ServerResponse> getCustomer(ServerRequest request){
        return ParameterValidator.getAndValidatePathParam(request, CUSTOMER_ID)
                .flatMap( id -> customerService.getCustomer(request.pathVariable(CUSTOMER_ID))
                        .flatMap(customer -> ServerResponse.ok().contentType(APPLICATION_JSON).body(BodyInserters.fromValue(customer))))
                .onErrorResume(IllegalArgumentException.class, GlobalErrorHandler::handleInvalidArgumentException)
                .onErrorResume(NotFoundException.class, GlobalErrorHandler::handleNotFoundException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    public Mono<ServerResponse> getCustomers(ServerRequest request){
        return ParameterValidator.getAndValidateQueryParam(request, "page", 0)
                .flatMap(page -> ParameterValidator.getAndValidateQueryParam(request, "size", 100)
                        .flatMap(size -> customerService.getCustomers(page, size, request.queryParam("firstName").orElse(""))
                .collectList()
                .flatMap(customers -> ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just(customers), List.class))))
                .onErrorResume(NotFoundException.class, GlobalErrorHandler::handleNotFoundException)
                .onErrorResume(IllegalArgumentException.class, GlobalErrorHandler::handleInvalidArgumentException)
                .onErrorResume(AuthenticationException.class, GlobalErrorHandler::handleAuthenticationException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    public Mono<ServerResponse> deleteCustomer(ServerRequest request){
        return ParameterValidator.getAndValidatePathParam(request, CUSTOMER_ID)
                .flatMap(id -> customerService.deleteCustomer(id)
                        .then(ServerResponse.noContent().build()))
                .onErrorResume(IllegalArgumentException.class, GlobalErrorHandler::handleInvalidArgumentException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);

    }

    private Mono<CustomerDto> validateCustomerObject(CustomerDto customerDto){
        Errors errors = new BeanPropertyBindingResult(customerDto, customerDto.getClass().getName());
        validator.validate(customerDto, errors);

        if (errors.hasErrors()) {
            throw new ConstraintViolationException("validation failed", errors.getAllErrors());
        } else {
            return Mono.just(customerDto);
        }
    }

}
