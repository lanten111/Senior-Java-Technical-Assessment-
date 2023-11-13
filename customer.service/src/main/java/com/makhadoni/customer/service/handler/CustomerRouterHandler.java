package com.makhadoni.customer.service.handler;

import com.makhadoni.customer.service.domain.Customer;
import com.makhadoni.customer.service.dto.CustomerDto;
import com.makhadoni.customer.service.exception.*;
import com.makhadoni.customer.service.service.CustomerService;
import com.makhadoni.customer.service.validator.RequestValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class CustomerRouterHandler {

    private final CustomerService customerService;
    private final RequestValidator validator;

    public CustomerRouterHandler(CustomerService customerService, RequestValidator validator) {
        this.customerService = customerService;
        this.validator = validator;
    }

    public Mono<ServerResponse> createOrUpdateCustomer(ServerRequest request){
        return request.bodyToMono(CustomerDto.class)
                .flatMap(this::validate)
                .flatMap(savedCustomer -> ServerResponse.ok().body(BodyInserters.fromValue(savedCustomer)))
                .onErrorResume(ConstraintViolationException.class, GlobalErrorHandler::handleContraintViolation)
                .onErrorResume(UserAlreadyExistsException.class, GlobalErrorHandler::handleAlreadyExistsException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    public Mono<ServerResponse> getCustomer(ServerRequest request){
        return customerService.getCustomer(request.pathVariable("id"))
                .switchIfEmpty(Mono.error(new MissingParameterException("path variable id required")))
                .flatMap(customer -> ServerResponse.ok().body(BodyInserters.fromValue(customer)))
                .onErrorResume(NotFoundException.class, GlobalErrorHandler::handleNotFoundException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    public Mono<ServerResponse> getCustomers(ServerRequest request){
        return ServerResponse.ok().body(customerService.getCustomers(), CustomerDto.class)
                .onErrorResume(NotFoundException.class, GlobalErrorHandler::handleNotFoundException)
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    public Mono<ServerResponse> deleteCustomer(ServerRequest request){
        return customerService.deleteCustomer(request.pathVariable("id"))
                .then(ServerResponse.noContent().build())
                .onErrorResume(Exception.class, GlobalErrorHandler::handleGenericException);
    }

    private Mono<CustomerDto> validate(CustomerDto customerDto){
        Errors errors = new BeanPropertyBindingResult(customerDto, "CustomerDto");
        validator.validate(customerDto, errors);

        if (errors.hasErrors()) {
            throw new ConstraintViolationException("validation failed", errors.getAllErrors());
        } else {
            return Mono.just(customerDto);
        }
    }
}
