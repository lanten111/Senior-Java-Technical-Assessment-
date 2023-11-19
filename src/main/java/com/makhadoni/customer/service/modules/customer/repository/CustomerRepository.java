package com.makhadoni.customer.service.modules.customer.repository;

import com.makhadoni.customer.service.modules.customer.domain.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

    Flux<Customer> findAllByFirstNameContainingIgnoreCaseOrderById(String name, Pageable pageable);

}
