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
                .flatMap(savedCustomer -> ServerResponse.ok().body(BodyInserters.fromValue(savedCustomer)))
                .onErrorResume(ConstraintViolationException.class, GlobalErrorHandler::handleConstraintViolation)
                .onErrorResume(CustomerAlreadyExistsException.class, GlobalErrorHandler::handleAlreadyExistsException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    public Mono<ServerResponse> updateCustomer(ServerRequest request){
        return request.bodyToMono(CustomerDto.class)
                .flatMap(this::validateCustomerObject)
                .flatMap(customerDto -> customerService.updateCustomer(customerDto))
                .flatMap(savedCustomer -> ServerResponse.ok().body(BodyInserters.fromValue(savedCustomer)))
                .onErrorResume(ConstraintViolationException.class, GlobalErrorHandler::handleConstraintViolation)
                .onErrorResume(CustomerAlreadyExistsException.class, GlobalErrorHandler::handleAlreadyExistsException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    public Mono<ServerResponse> getCustomer(ServerRequest request){

        return Mono.justOrEmpty(request.pathVariable("customerId"))
                .flatMap(customerId -> {
                    if (isPresentAndNumeric(customerId)) {
                        return customerService.getCustomer(request.pathVariable("customerId"))
                                .flatMap(customer -> ServerResponse.ok().contentType(APPLICATION_JSON).body(BodyInserters.fromValue(customer)));
                    }else {
                        return Mono.error(new InvalidArgumentException("Invalid or missing required numeric path variable: customer id"));
                    }
                }).onErrorResume(InvalidArgumentException.class, GlobalErrorHandler::handleInvalidArgumentException)
                .onErrorResume(NotFoundException.class, GlobalErrorHandler::handleNotFoundException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    public Mono<ServerResponse> getCustomers(ServerRequest request){
        return getQueryParam(request, "page", 0)
                .flatMap(page -> getQueryParam(request, "size", 100)
                        .flatMap(size -> customerService.getCustomers(page, size, request.queryParam("firstName").orElse(""))
                .collectList()
                .flatMap(customers -> ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just(customers), List.class))))
                .onErrorResume(NotFoundException.class, GlobalErrorHandler::handleNotFoundException)
                .onErrorResume(InvalidArgumentException.class, GlobalErrorHandler::handleInvalidArgumentException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    public Mono<ServerResponse> deleteCustomer(ServerRequest request){
        String customerId = request.pathVariable("id");
        if (!isPresentAndNumeric(customerId)) {
            throw new IllegalArgumentException("Invalid or missing required numeric path variable: customer id");
        }
        return customerService.deleteCustomer(customerId)
                .then(ServerResponse.noContent().build())
                .onErrorResume(InvalidArgumentException.class, GlobalErrorHandler::handleInvalidArgumentException)
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

    private Mono<Integer> getQueryParam(ServerRequest request, String param, int defaultValue){
        return Mono.just(request.queryParam(param)
                .map(value -> {
                    if (!isNumeric(value)) {
                        throw new InvalidArgumentException("Invalid or missing required numeric parameter: page");
                    } else if (value.isEmpty()) {
                        return 0;
                    } else {
                        return Integer.parseInt(value);
                    }
                }).get());

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
