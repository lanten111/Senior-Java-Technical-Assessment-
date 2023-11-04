package com.makhadoni.customer.service.service;

import com.makhadoni.customer.service.domain.Customer;
import com.makhadoni.customer.service.dto.CustomerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {

    Flux<Customer> getCustomers();

    Void createCustomer(CustomerDto customerDto);

    Mono<Customer> getCustomer(Long customerId);

    Mono<CustomerDto> updateCustomer(CustomerDto customerDto);

    void deleteCustomer(CustomerDto customerDto);

}
