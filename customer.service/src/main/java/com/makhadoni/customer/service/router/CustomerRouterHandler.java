package com.makhadoni.customer.service.router;

import com.makhadoni.customer.service.dto.CustomerDto;
import com.makhadoni.customer.service.service.CustomerService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CustomerRouterHandler {

    private final CustomerService customerService;

    public CustomerRouterHandler(CustomerService customerService) {
        this.customerService = customerService;
    }

    public Mono<ServerResponse> createCustomer(ServerRequest request){
        return request.bodyToMono(CustomerDto.class).
                flatMap(customerDto -> customerService.createCustomer(customerDto).
                        flatMap(savedCustomer -> ServerResponse.ok().body(BodyInserters.fromValue(savedCustomer))));
    }

    public Mono<ServerResponse> getCustomer(ServerRequest request){
        return customerService.getCustomer(request.pathVariable("id")).
                flatMap(customer -> ServerResponse.ok().body(BodyInserters.fromValue(customer)));
    }

    public Mono<ServerResponse> getCustomers(ServerRequest request){
        return ServerResponse.ok().body(customerService.getCustomers(), CustomerDto.class);
    }

    public Mono<ServerResponse> updateCustomer(ServerRequest request){
        return request.bodyToMono(CustomerDto.class).
                flatMap(customerDto -> customerService.createCustomer(customerDto).
                        flatMap(savedCustomer -> ServerResponse.ok().body(BodyInserters.fromValue(savedCustomer))));
    }

    public Mono<ServerResponse> deleteCustomer(ServerRequest request){
        return customerService.deleteCustomer(request.pathVariable("id")).
                flatMap(customer -> ServerResponse.ok().body(BodyInserters.fromValue(customer)));
    }
}
