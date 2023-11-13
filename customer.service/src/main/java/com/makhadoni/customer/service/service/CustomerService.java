package com.makhadoni.customer.service.service;

import com.makhadoni.customer.service.domain.Customer;
import com.makhadoni.customer.service.dto.CustomerDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {

    Flux<CustomerDto> getCustomers();

    Mono<CustomerDto> createOrUpdateCustomer(CustomerDto customerDto);

    Mono<CustomerDto> getCustomer(String customerId);


    Mono<Void> deleteCustomer(String id);

}
