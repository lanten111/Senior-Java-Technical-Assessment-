package com.makhadoni.customer.service.modules.customer.service;

import com.makhadoni.customer.service.modules.customer.dto.CustomerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface CustomerService {

    Flux<CustomerDto> getCustomers(int page, int size, String firstName);

    Mono<CustomerDto> createCustomer(CustomerDto customerDto);

    Mono<CustomerDto> updateCustomer(CustomerDto customerDto);

    Mono<CustomerDto> getCustomer(String customerId);

    Mono<Void> deleteCustomer(int id);

}
