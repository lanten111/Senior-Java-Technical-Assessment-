package com.makhadoni.customer.service;

import com.makhadoni.customer.service.modules.customer.domain.Customer;
import com.makhadoni.customer.service.modules.customer.dto.CustomerDto;
import com.makhadoni.customer.service.modules.auth.domain.User;
import com.makhadoni.customer.service.modules.auth.dto.UserDto;

public class TestSuccessFactory {

    public static Customer getCustomer(){

        Customer customer = new Customer();
        customer.setLastName("hamilton");
        customer.setFirstName("lewis");
        customer.setAge(44);
        customer.setEmail("lewis@mercedes.com");
        return customer;
    }

    public static CustomerDto getCustomerDto(){

        CustomerDto customerDto = new CustomerDto();
        customerDto.setLastName("hamilton");
        customerDto.setFirstName("lewis");
        customerDto.setAge(44);
        customerDto.setEmail("lewis@mercedes.com");
        return customerDto;
    }

    public static UserDto getUserDto(){
        UserDto userDto = new UserDto();
        userDto.setPassword("password123");
        userDto.setUsername("mario");
        return userDto;
    }

    public static User getUser(){
        User user = new User();
        user.setPassword("password123");
        user.setUsername("mario");
        return user;
    }
}
