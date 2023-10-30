package com.makhadoni.customer.service.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collation = "customer")
public class Customer {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private int age;
}
