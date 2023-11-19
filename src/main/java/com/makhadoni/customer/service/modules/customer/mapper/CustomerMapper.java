package com.makhadoni.customer.service.modules.customer.mapper;

import com.makhadoni.customer.service.modules.customer.domain.Customer;
import com.makhadoni.customer.service.modules.customer.dto.CustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerMapper {

    CustomerMapper MAPPER = Mappers.getMapper(CustomerMapper.class);

    CustomerDto mapTo(Customer customer);
    Customer mapFrom(CustomerDto customerDto);

}
