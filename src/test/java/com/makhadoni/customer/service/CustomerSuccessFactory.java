package com.makhadoni.customer.service;

import com.makhadoni.customer.service.modules.customer.domain.Customer;
import com.makhadoni.customer.service.modules.customer.dto.CustomerDto;

public class CustomerSuccessFactory {

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
}
