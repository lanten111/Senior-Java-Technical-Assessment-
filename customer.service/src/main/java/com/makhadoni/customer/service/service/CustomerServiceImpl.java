package com.makhadoni.customer.service.service;

import com.makhadoni.customer.service.domain.Customer;
import com.makhadoni.customer.service.dto.CustomerDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Override
    public Flux<Customer> getCustomers() {
        return null;
    }

    @Override
    public Void createCustomer(CustomerDto customerDto) {
        return null;
    }

    @Override
    public Mono<Customer> getCustomer(Long customerId) {
        return null;
    }

    @Override
    public Mono<CustomerDto> updateCustomer(CustomerDto customerDto) {
        return null;
    }

    @Override
    public void deleteCustomer(CustomerDto customerDto) {

    }
}
