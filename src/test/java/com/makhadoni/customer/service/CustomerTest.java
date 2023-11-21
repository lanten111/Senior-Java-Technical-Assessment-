package com.makhadoni.customer.service;

import com.makhadoni.customer.service.cache.CustomerCacheService;
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
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.*;

class CustomerTest  {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CustomerCacheService customerCacheService;

    @InjectMocks
    CustomerServiceImpl customerService;

    private final CustomerDto customerDto = TestSuccessFactory.getCustomerDto();

    private final Customer customer = TestSuccessFactory.getCustomer();

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(customerService, "customerCacheTimeout", "5");
    }

    @Test
    void canSuccessfullyGetCustomers(){

        Mockito.when(customerCacheService.getList(Mockito.anyString())).thenReturn(Flux.empty());
        Mockito.when(customerRepository.findAllByFirstNameContainingIgnoreCaseOrderById(anyString(), any())).thenReturn(Flux.just(customer));
        Mockito.when(customerCacheService.setList(Mockito.anyString(), Mockito.anyList(), any())).thenReturn(Flux.just(1).count());


        StepVerifier.create(customerService.getCustomers(1, 1, ""))
                .expectNextMatches(customerDto ->
                        customerDto.getFirstName().equals(customer.getFirstName()) &&
                                customerDto.getEmail().equals(customer.getEmail()) &&
                                customerDto.getAge() == customer.getAge() &&
                                Objects.equals(customerDto.getLastName(), customer.getLastName()) &&
                                Objects.equals(customerDto.getId(), customer.getId())
                ).verifyComplete();
    }


    @Test
    void canSuccessfullyGetCustomersFromCache(){

        Mockito.when(customerCacheService.getList(Mockito.anyString())).thenReturn(Flux.just(customerDto));

        StepVerifier.create(customerService.getCustomers(1, 1, ""))
                .expectNextMatches(customerDto ->
                        customerDto.getFirstName().equals(customer.getFirstName()) &&
                                customerDto.getEmail().equals(customer.getEmail()) &&
                                customerDto.getAge() == customer.getAge() &&
                                Objects.equals(customerDto.getLastName(), customer.getLastName()) &&
                                Objects.equals(customerDto.getId(), customer.getId())
                ).verifyComplete();
    }

    @Test
    void CanCreateOrUpdateCustomer(){

        Mockito.when(customerRepository.save(any())).thenReturn(Mono.just(customer));

        StepVerifier.create(customerService.createCustomer(customerDto))
                .expectNextMatches(customerDto ->
                        customerDto.getFirstName().equals(customer.getFirstName()) &&
                                customerDto.getEmail().equals(customer.getEmail()) &&
                                customerDto.getAge() == customer.getAge() &&
                                Objects.equals(customerDto.getLastName(), customer.getLastName()) &&
                                Objects.equals(customerDto.getId(), customer.getId())
                ).verifyComplete();
    }

    @Test
    void canSuccessfullyGetCustomer(){

        Mockito.when(customerCacheService.getValue(Mockito.anyString())).thenReturn(Mono.empty());
        Mockito.when(customerRepository.findById(anyInt())).thenReturn(Mono.just(customer));
        Mockito.when(customerCacheService.setValue(any(), any(), any())).thenReturn(Mono.just(true));


        StepVerifier.create(customerService.getCustomer("1"))
                .expectNextMatches(customerDto ->
                    customerDto.getFirstName().equals(customer.getFirstName()) &&
                            customerDto.getEmail().equals(customer.getEmail()) &&
                            customerDto.getAge() == customer.getAge() &&
                            Objects.equals(customerDto.getLastName(), customer.getLastName()) &&
                            Objects.equals(customerDto.getId(), customer.getId())
                ).verifyComplete();
    }

    @Test
    void canSuccessfullyGetCustomerFromCache(){


        Mockito.when(customerCacheService.getValue(Mockito.anyString())).thenReturn(Mono.just(customerDto));

        StepVerifier.create(customerService.getCustomer("1"))
                .expectNextMatches(customerDto ->
                        customerDto.getFirstName().equals(customer.getFirstName()) &&
                                customerDto.getEmail().equals(customer.getEmail()) &&
                                customerDto.getAge() == customer.getAge() &&
                                Objects.equals(customerDto.getLastName(), customer.getLastName()) &&
                                Objects.equals(customerDto.getId(), customer.getId())
                ).verifyComplete();
    }

    @Test
    void canThrowNotFoundException(){

        Mockito.when(customerCacheService.getValue(Mockito.anyString())).thenReturn(Mono.empty());
        Mockito.when(customerRepository.findById(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(customerService.getCustomer("1"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void canSuccessfullyDeleteCustomer(){

        Mockito.when(customerRepository.deleteById(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(customerService.deleteCustomer(1))
                .verifyComplete();
    }
}



