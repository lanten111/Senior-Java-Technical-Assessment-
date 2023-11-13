package com.makhadoni.customer.service.service;

import com.makhadoni.customer.service.cache.CacheService;
import com.makhadoni.customer.service.dto.CustomerDto;
import com.makhadoni.customer.service.mapper.CustomerMapper;
import com.makhadoni.customer.service.repository.CustomerRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper = CustomerMapper.MAPPER;
    private final CacheService cache;

    public CustomerServiceImpl(CustomerRepository customerRepository, CacheService cache) {
        this.customerRepository = customerRepository;
        this.cache = cache;
    }

    @Override
    @Cacheable(value = "customer")
    public Flux<CustomerDto> getCustomers() {
       return customerRepository.findAll().map(CustomerMapper.MAPPER::customerToCustomerDto).defaultIfEmpty().doOnError();
    }

    @Override
    public Mono<CustomerDto> createCustomer(CustomerDto customerDto) {
        return customerRepository.save(mapper.customerDtoToCustomer(customerDto)).
                map(savedCustomer -> mapper.customerToCustomerDto(savedCustomer)).doOnError();
    }

    @Override
    public Mono<CustomerDto> getCustomer(String customerId) {
       return cache.getValue(customerId).
               map(customer -> mapper.customerToCustomerDto(customer)).
               switchIfEmpty(Mono.defer(() -> customerRepository.findById(Integer.parseInt(customerId)).
                       flatMap(customer -> cache.setValue(String.valueOf(customer.getId()), customer).
                               map(isCached -> {
           if (isCached){
               return mapper.customerToCustomerDto(customer);
           } else {
               throw new RuntimeException("stuff");
           }
       })))).doOnError();

    }

    @Override
    public Mono<CustomerDto> updateCustomer(CustomerDto customerDto) {
        return customerRepository.save(mapper.customerDtoToCustomer(customerDto)).
                map(updatedCustomer -> mapper.customerToCustomerDto(updatedCustomer)).defaultIfEmpty().doOnError();
    }

    @Override
    public Mono<Void> deleteCustomer(String customerId) {
        return customerRepository.deleteById(Integer.parseInt(customerId)).defaultIfEmpty().doOnError();
    }
}
