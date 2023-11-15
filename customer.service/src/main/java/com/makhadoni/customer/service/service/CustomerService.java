package com.makhadoni.customer.service.service;

import com.makhadoni.customer.service.domain.Customer;
import com.makhadoni.customer.service.dto.CustomerDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface CustomerService {

    Flux<CustomerDto> getCustomers(int page, int size, String firstName);

    Mono<CustomerDto> createCustomer(CustomerDto customerDto);

    Mono<CustomerDto> updateCustomer(CustomerDto customerDto);

    Mono<CustomerDto> getCustomer(String customerId);

    Mono<Void> deleteCustomer(String id);

}
