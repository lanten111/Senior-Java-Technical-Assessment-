package com.makhadoni.customer.service.service;

import com.makhadoni.customer.service.cache.CacheService;
import com.makhadoni.customer.service.dto.CustomerDto;
import com.makhadoni.customer.service.exception.*;
import com.makhadoni.customer.service.mapper.CustomerMapper;
import com.makhadoni.customer.service.repository.CustomerRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper = CustomerMapper.MAPPER;
    private final CacheService cache;

    public CustomerServiceImpl(CustomerRepository customerRepository, CacheService cache) {
        this.repository = customerRepository;
        this.cache = cache;
    }

    @Override
    public Flux<CustomerDto> getCustomers(int page, int size, String firstName) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return cache.getList(createPageableCustomerKey(page, size, firstName))
                .switchIfEmpty(Flux.defer(() -> repository.findAllByFirstNameContainingIgnoreCaseOrderById(firstName, pageable)
                        .switchIfEmpty(Mono.error(new NotFoundException("No customers found")))
                        .map(customers -> mapper.customerToCustomerDto(customers))
                        .collectList()
                        .doOnNext(customerDtos -> cache.setList(createPageableCustomerKey(page, size, firstName), customerDtos).subscribe())
                        .flatMapMany(Flux::fromIterable)))
                .onErrorResume(IllegalArgumentException.class, ex -> Mono.error(new InvalidArgumentException("Not valid customer id")));
    }


    public Mono<CustomerDto> createOrUpdateCustomer(CustomerDto customerDto) {
        return repository.save(mapper.customerDtoToCustomer(customerDto)).
                onErrorResume(DuplicateKeyException.class, ex -> Mono.error(new CustomerAlreadyExistsException(ex.getCause().getMessage())))
                .map(savedCustomer -> mapper.customerToCustomerDto(savedCustomer))
                .onErrorResume(WebClientResponseException.InternalServerError.class, exception -> Mono.error(new GeneralException(exception.getMessage())));
    }

    @Override
    public Mono<CustomerDto> createCustomer(CustomerDto customerDto) {
        return createOrUpdateCustomer(customerDto);
    }


    @Override
    public Mono<CustomerDto> updateCustomer(CustomerDto customerDto) {
        return createOrUpdateCustomer(customerDto);
    }

    @Override
    public Mono<CustomerDto> getCustomer(String customerId) {
       return cache.getValue(customerId)
               .switchIfEmpty(Mono.defer(() -> repository.findById(Integer.parseInt(customerId))
                       .switchIfEmpty(Mono.error(new NotFoundException("user with id "+ customerId + " not found")))
                       .map(customer -> mapper.customerToCustomerDto(customer))
                       .doOnNext(customerDto -> cache.setValue(customerId,customerDto).subscribe())));
    }

    @Override
    public Mono<Void> deleteCustomer(String customerId) {
        return repository.deleteById(Integer.parseInt(customerId))
                .onErrorResume(IllegalArgumentException.class, ex -> Mono.error(new InvalidArgumentException(customerId + " Not valid customer id")))
                .onErrorResume(Exception.class, ex -> Mono.error(new GeneralException(ex.getCause().getMessage())));
    }

    private String createPageableCustomerKey(int page, int size, String firstName){
        return String.valueOf(page) + String.valueOf(size) + firstName;
    }
}
