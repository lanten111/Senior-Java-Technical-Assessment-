package com.makhadoni.customer.service.service;

import com.makhadoni.customer.service.cache.CacheService;
import com.makhadoni.customer.service.dto.CustomerDto;
import com.makhadoni.customer.service.exception.*;
import com.makhadoni.customer.service.mapper.CustomerMapper;
import com.makhadoni.customer.service.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper = CustomerMapper.MAPPER;
    private final CacheService cache;

    private static final Logger logger = Logger.getLogger(CustomerServiceImpl.class.getName());

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, CacheService cache) {
        this.repository = customerRepository;
        this.cache = cache;
    }

    @Override
    public Flux<CustomerDto> getCustomers(int page, int size, String firstName) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return cache.getList(createPageableCustomerKey(page, size, firstName))
                .switchIfEmpty(Flux.defer(() -> repository.findAllByFirstNameContainingIgnoreCaseOrderById(firstName, pageable)
                        .switchIfEmpty(Flux.error(new NotFoundException("No customers found")))
                        .map(customers -> mapper.customerToCustomerDto(customers))
                        .collectList()
                        .doOnNext(customerDtos -> cache.setList
                                (createPageableCustomerKey(page, size, firstName), customerDtos, Duration.ofHours(6)).subscribe())
                        .flatMapMany(Flux::fromIterable)));
    }


    public Mono<CustomerDto> createOrUpdateCustomer(CustomerDto customerDto) {
        return repository.save(mapper.customerDtoToCustomer(customerDto))
                .onErrorResume(DuplicateKeyException.class, ex -> Mono
                        .error(new CustomerAlreadyExistsException("Customer with email: "+ customerDto.getEmail() + " already exist")))
                .map(savedCustomer -> mapper.customerToCustomerDto(savedCustomer));
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
               .log("*************************************************got customer from cach", Level.WARNING, SignalType.ON_NEXT)
               .switchIfEmpty(Mono.defer(() -> repository.findById(Integer.parseInt(customerId))
                       .switchIfEmpty(Mono.error(new NotFoundException("Customer with id "+ customerId + " does not exisit")))
                       .map(customer -> mapper.customerToCustomerDto(customer))
                       .doOnNext(customerDto -> cache.setValue(customerId,customerDto, Duration.ofHours(3)).subscribe())));
    }

    @Override
    public Mono<Void> deleteCustomer(int customerId) {
        return repository.deleteById(customerId)
                .doOnNext(r -> {
                    logger.log(Level.WARNING, "***********************************************Deleted customer with id: "+ customerId);
                })
                .doOnNext(t -> cache.evictAll());
    }

    private String createPageableCustomerKey(int page, int size, String firstName){
        return String.valueOf(page) + String.valueOf(size) + firstName;
    }
}
