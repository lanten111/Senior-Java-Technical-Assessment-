package com.makhadoni.customer.service.service;

import com.makhadoni.customer.service.dto.CustomerDto;
import com.makhadoni.customer.service.mapper.CustomerMapper;
import com.makhadoni.customer.service.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper = CustomerMapper.MAPPER;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Flux<CustomerDto> getCustomers() {
       return customerRepository.findAll().map(CustomerMapper.MAPPER::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDto> createCustomer(CustomerDto customerDto) {
        return customerRepository.save(mapper.customerDtoToCustomer(customerDto)).
                map(savedCustomer -> mapper.customerToCustomerDto(savedCustomer));
    }

    @Override
    public Mono<CustomerDto> getCustomer(String customerId) {
        return customerRepository.findById(Integer.parseInt(customerId)).
                map(customer -> mapper.customerToCustomerDto(customer));
    }

    @Override
    public Mono<CustomerDto> updateCustomer(CustomerDto customerDto) {
        return customerRepository.save(mapper.customerDtoToCustomer(customerDto)).
                map(updatedCustomer -> mapper.customerToCustomerDto(updatedCustomer));
    }

    @Override
    public Mono<Void> deleteCustomer(String customerId) {
        return customerRepository.deleteById(Integer.parseInt(customerId));
    }
}
