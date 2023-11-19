package com.makhadoni.customer.service.modules.auth.service;

import com.makhadoni.customer.service.modules.auth.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<String> createUser(UserDto userDto);

    Mono<UserDto> getUser(String username);
}
