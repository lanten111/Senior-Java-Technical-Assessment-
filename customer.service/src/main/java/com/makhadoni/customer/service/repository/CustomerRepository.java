package com.makhadoni.customer.service.repository;

import com.makhadoni.customer.service.domain.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

    Flux<Customer> getAllByFirstName(String firstName);

}
