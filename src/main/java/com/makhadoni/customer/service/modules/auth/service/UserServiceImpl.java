package com.makhadoni.customer.service.modules.auth.service;

import com.makhadoni.customer.service.exception.AlreadyExistsException;
import com.makhadoni.customer.service.modules.auth.Repository.UserRepository;
import com.makhadoni.customer.service.modules.auth.dto.UserDto;
import com.makhadoni.customer.service.modules.auth.mapper.UserMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository repository;

    private final UserMapper mapper = UserMapper.MAPPER;

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<UserDto> getUser(String username){
        return repository.findByUsername(username).map( user -> mapper.mapTo(user));
    }

    @Override
    public Mono<String> createUser(UserDto userDto) {
        return repository.save(mapper.MapFrom(userDto))
                .flatMap(user -> Mono.just("User with username "+user.getUsername()+ " has successfully been created"))
                .doOnNext(r -> logger.log(Level.INFO, "Created new customer with username: "+userDto.getUsername()))
                .onErrorResume(DuplicateKeyException.class, ex -> {
                    logger.log(Level.WARNING, "Caught DuplicateKeyException during user creation", ex);
                    return Mono.error(new AlreadyExistsException("User with username: " + userDto.getUsername() + " already exists"));
                });
    }
}
