package com.makhadoni.customer.service.repository;

import com.makhadoni.customer.service.domain.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

    public interface
}
