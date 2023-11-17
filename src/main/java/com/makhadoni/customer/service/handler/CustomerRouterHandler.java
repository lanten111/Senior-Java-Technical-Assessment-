package com.makhadoni.customer.service.handler;

import com.makhadoni.customer.service.dto.CustomerDto;
import com.makhadoni.customer.service.exception.*;
import com.makhadoni.customer.service.service.CustomerService;
import com.makhadoni.customer.service.validator.RequestValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class CustomerRouterHandler {

    private final CustomerService customerService;
    private final RequestValidator validator;

    public CustomerRouterHandler(CustomerService customerService, RequestValidator validator) {
        this.customerService = customerService;
        this.validator = validator;
    }

    public Mono<ServerResponse> createCustomer(ServerRequest request){
        return request.bodyToMono(CustomerDto.class)
                .flatMap(this::validateCustomerObject)
                .flatMap(customerDto -> customerService.createCustomer(customerDto))
                .flatMap(savedCustomer -> ServerResponse.ok().contentType(APPLICATION_JSON).body(BodyInserters.fromValue(savedCustomer)))
                .onErrorResume(ConstraintViolationException.class, GlobalErrorHandler::handleConstraintViolation)
                .onErrorResume(CustomerAlreadyExistsException.class, GlobalErrorHandler::handleAlreadyExistsException)
                .onErrorResume(IllegalArgumentException.class, GlobalErrorHandler::handleInvalidArgumentException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    public Mono<ServerResponse> updateCustomer(ServerRequest request){
        return request.bodyToMono(CustomerDto.class)
                .flatMap(this::validateCustomerObject)
                .flatMap(customerDto -> customerService.updateCustomer(customerDto))
                .flatMap(savedCustomer -> ServerResponse.ok().contentType(APPLICATION_JSON).body(BodyInserters.fromValue(savedCustomer)))
                .onErrorResume(ConstraintViolationException.class, GlobalErrorHandler::handleConstraintViolation)
                .onErrorResume(CustomerAlreadyExistsException.class, GlobalErrorHandler::handleAlreadyExistsException)
                .onErrorResume(IllegalArgumentException.class, GlobalErrorHandler::handleInvalidArgumentException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    public Mono<ServerResponse> getCustomer(ServerRequest request){
        return getAndValidatePathParam(request, "customerId")
                .flatMap( id -> customerService.getCustomer(request.pathVariable("customerId"))
                        .flatMap(customer -> ServerResponse.ok().contentType(APPLICATION_JSON).body(BodyInserters.fromValue(customer))))
                .onErrorResume(IllegalArgumentException.class, GlobalErrorHandler::handleInvalidArgumentException)
                .onErrorResume(NotFoundException.class, GlobalErrorHandler::handleNotFoundException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    public Mono<ServerResponse> getCustomers(ServerRequest request){
        return getAndValidateQueryParam(request, "page", 0)
                .flatMap(page -> getAndValidateQueryParam(request, "size", 100)
                        .flatMap(size -> customerService.getCustomers(page, size, request.queryParam("firstName").orElse(""))
                .collectList()
                .flatMap(customers -> ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just(customers), List.class))))
                .onErrorResume(NotFoundException.class, GlobalErrorHandler::handleNotFoundException)
                .onErrorResume(IllegalArgumentException.class, GlobalErrorHandler::handleInvalidArgumentException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    public Mono<ServerResponse> deleteCustomer(ServerRequest request){
        return getAndValidatePathParam(request, "customerId")
                .flatMap(id -> customerService.deleteCustomer(id)
                        .then(ServerResponse.noContent().build()))
                .onErrorResume(IllegalArgumentException.class, GlobalErrorHandler::handleInvalidArgumentException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);

    }

    private Mono<CustomerDto> validateCustomerObject(CustomerDto customerDto){
        Errors errors = new BeanPropertyBindingResult(customerDto, "CustomerDto");
        validator.validate(customerDto, errors);

        if (errors.hasErrors()) {
            throw new ConstraintViolationException("validation failed", errors.getAllErrors());
        } else {
            return Mono.just(customerDto);
        }
    }

    private Mono<Integer> getAndValidateQueryParam(ServerRequest request, String queryParam, int defaultValue){
        return Mono.just(request.queryParam(queryParam)
                .map(value -> {
                    if (!isNumeric(value)) {
                        throw new IllegalArgumentException("Invalid query parameter: "+value);
                    } else if (value.isEmpty()) {
                        return defaultValue;
                    } else {
                        return Integer.parseInt(value);
                    }
                }).get());

    }

    private Mono<Integer> getAndValidatePathParam(ServerRequest request, String pathParam){
        return Mono.just(request.pathVariable(pathParam))
                .map(value -> {
                    if (!isPresentAndNumeric(value)){
                        throw new IllegalArgumentException("Invalid or missing required numeric path variable: "+value );
                    } else {
                        return Integer.parseInt(value);
                    }
                });
    }

    private boolean isPresentAndNumeric(String str) {
        return str != null && isNumeric(str);
    }

    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
