package com.makhadoni.customer.service.service;

import com.makhadoni.customer.service.cache.CacheService;
import com.makhadoni.customer.service.dto.CustomerDto;
import com.makhadoni.customer.service.exception.GeneralException;
import com.makhadoni.customer.service.exception.NotFoundException;
import com.makhadoni.customer.service.exception.UserAlreadyExistsException;
import com.makhadoni.customer.service.mapper.CustomerMapper;
import com.makhadoni.customer.service.repository.CustomerRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
       return customerRepository.findAll().map(CustomerMapper.MAPPER::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDto> createOrUpdateCustomer(CustomerDto customerDto) {
        return customerRepository.save(mapper.customerDtoToCustomer(customerDto)).
        onErrorResume(DuplicateKeyException.class, ex -> Mono.error(new UserAlreadyExistsException(ex.getCause().getMessage())))
                .map(savedCustomer -> mapper.customerToCustomerDto(savedCustomer))
                .onErrorResume(WebClientResponseException.InternalServerError.class, exception -> Mono.error(new GeneralException(exception.getMessage())));
    }

    @Override
    public Mono<CustomerDto> getCustomer(String customerId) {
       return cache.getValue(customerId).
               map(customer -> mapper.customerToCustomerDto(customer)).
               switchIfEmpty(Mono.defer(() -> customerRepository.findById(Integer.parseInt(customerId))
                               .switchIfEmpty(Mono.error(new NotFoundException("user with id "+ customerId + " not found"))).
                       flatMap(customer -> cache.setValue(String.valueOf(customer.getId()), customer).
                               handle((isCached, sink) -> {
           if (isCached){
               sink.next(mapper.customerToCustomerDto(customer));
           } else {
               sink.error(new GeneralException(""));
           }
       }))));

    }

    @Override
    public Mono<Void> deleteCustomer(String customerId) {
        return customerRepository.deleteById(Integer.parseInt(customerId))
                . onErrorResume(Exception.class, ex -> Mono.error(new GeneralException(ex.getCause().getMessage())));
    }
}
