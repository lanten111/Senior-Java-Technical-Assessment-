package com.makhadoni.customer.service.dto;

import lombok.Data;

@Data
public class CustomerDto {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private int age;
}
