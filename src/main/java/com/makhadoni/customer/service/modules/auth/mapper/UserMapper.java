package com.makhadoni.customer.service.modules.auth.mapper;

import com.makhadoni.customer.service.modules.auth.domain.User;
import com.makhadoni.customer.service.modules.auth.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    UserDto mapTo(User user);
    User mapFrom(UserDto userDto);
}
