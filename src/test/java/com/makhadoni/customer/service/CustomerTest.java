package com.makhadoni.customer.service;

import com.makhadoni.customer.service.cache.CacheService;
import com.makhadoni.customer.service.modules.customer.domain.Customer;
import com.makhadoni.customer.service.modules.customer.dto.CustomerDto;
import com.makhadoni.customer.service.exception.NotFoundException;
import com.makhadoni.customer.service.modules.customer.repository.CustomerRepository;
import com.makhadoni.customer.service.modules.customer.service.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;

public class CustomerTest  {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CacheService cacheService;

    @InjectMocks
    CustomerServiceImpl customerService;

    private final CustomerDto customerDto = TestSuccessFactory.getCustomerDto();

    private final Customer customer = TestSuccessFactory.getCustomer();

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void canSuccessfullyGetCustomers(){

        Mockito.when(cacheService.getList(Mockito.anyString())).thenReturn(Flux.empty());
        Mockito.when(customerRepository.findAllByFirstNameContainingIgnoreCaseOrderById(anyString(), any())).thenReturn(Flux.just(customer));
        Mockito.when(cacheService.setList(Mockito.anyString(), Mockito.anyList(), any())).thenReturn(Flux.just(1).count());


        StepVerifier.create(customerService.getCustomers(1, 1, ""))
                .expectNextMatches(customerDto ->
                        customerDto.getFirstName().equals(customer.getFirstName()) &&
                                customerDto.getEmail().equals(customer.getEmail()) &&
                                customerDto.getAge() == customer.getAge() &&
                                customerDto.getLastName() == customer.getLastName() &&
                                customerDto.getId() == customer.getId()
                ).verifyComplete();
    }


    @Test
    public void canSuccessfullyGetCustomersFromCache(){

        Mockito.when(cacheService.getList(Mockito.anyString())).thenReturn(Flux.just(customerDto));

        StepVerifier.create(customerService.getCustomers(1, 1, ""))
                .expectNextMatches(customerDto ->
                        customerDto.getFirstName().equals(customer.getFirstName()) &&
                                customerDto.getEmail().equals(customer.getEmail()) &&
                                customerDto.getAge() == customer.getAge() &&
                                customerDto.getLastName() == customer.getLastName() &&
                                customerDto.getId() == customer.getId()
                ).verifyComplete();
    }

    @Test
    public void CanCreateOrUpdateCustomer(){

        Mockito.when(customerRepository.save(any())).thenReturn(Mono.just(customer));

        StepVerifier.create(customerService.createCustomer(customerDto))
                .expectNextMatches(customerDto ->
                        customerDto.getFirstName().equals(customer.getFirstName()) &&
                                customerDto.getEmail().equals(customer.getEmail()) &&
                                customerDto.getAge() == customer.getAge() &&
                                customerDto.getLastName() == customer.getLastName() &&
                                customerDto.getId() == customer.getId()
                ).verifyComplete();
    }

    @Test
    public void canSuccessfullyGetCustomer(){

        Mockito.when(cacheService.getValue(Mockito.anyString())).thenReturn(Mono.empty());
        Mockito.when(customerRepository.findById(anyInt())).thenReturn(Mono.just(customer));
        Mockito.when(cacheService.setValue(Mockito.anyString(), Mockito.any(CustomerDto.class), any())).thenReturn(Mono.just(true));


        StepVerifier.create(customerService.getCustomer("1"))
                .expectNextMatches(customerDto ->
                    customerDto.getFirstName().equals(customer.getFirstName()) &&
                            customerDto.getEmail().equals(customer.getEmail()) &&
                            customerDto.getAge() == customer.getAge() &&
                            customerDto.getLastName() == customer.getLastName() &&
                            customerDto.getId() == customer.getId()
                ).verifyComplete();
    }

    @Test
    public void canSuccessfullyGetCustomerFromCache(){

        Mockito.when(cacheService.getValue(Mockito.anyString())).thenReturn(Mono.just(customerDto));

        StepVerifier.create(customerService.getCustomer("1"))
                .expectNextMatches(customerDto ->
                        customerDto.getFirstName().equals(customer.getFirstName()) &&
                                customerDto.getEmail().equals(customer.getEmail()) &&
                                customerDto.getAge() == customer.getAge() &&
                                customerDto.getLastName() == customer.getLastName() &&
                                customerDto.getId() == customer.getId()
                ).verifyComplete();
    }

    @Test
    public void canThrowNotFoundException(){

        Mockito.when(cacheService.getValue(Mockito.anyString())).thenReturn(Mono.empty());
        Mockito.when(customerRepository.findById(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(customerService.getCustomer("1"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    public void canSuccessfullyDeleteCustomer(){

        Mockito.when(customerRepository.deleteById(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(customerService.deleteCustomer(1))
                .verifyComplete();
    }
}



