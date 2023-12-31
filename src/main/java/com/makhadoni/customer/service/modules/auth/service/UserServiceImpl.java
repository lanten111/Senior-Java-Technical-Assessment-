package com.makhadoni.customer.service.modules.auth.service;

import com.makhadoni.customer.service.cache.UserCacheService;
import com.makhadoni.customer.service.exception.AlreadyExistsException;
import com.makhadoni.customer.service.modules.auth.Repository.UserRepository;
import com.makhadoni.customer.service.modules.auth.dto.UserDto;
import com.makhadoni.customer.service.modules.auth.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository repository;
    private static final UserMapper mapper = UserMapper.MAPPER;
    private final UserCacheService cache;

    @Value("${spring.data.redis.timeouts.user:2}")
    private int userCacheTimeout;

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    public UserServiceImpl(UserRepository repository, UserCacheService cache) {
        this.repository = repository;
        this.cache = cache;
    }

    @Override
    public Mono<UserDto> getUser(String name){
        return cache.getValue(name)
                .doOnNext(r -> logger.info( "getting user with username: "+ name))
                .switchIfEmpty(Mono.defer(() -> repository.findByUsername(name)
                        .doOnNext(user -> logger.log(Level.INFO,"User not found in cache. Fallback to repository. Username: {}", name))
                        .map(mapper::mapTo)
                        .doOnNext(userDto -> {
                            cache.setValue(name, userDto, Duration.ofHours(userCacheTimeout)).subscribe();
                            logger.info( "Cached user for username: " + name);
                        })
                ));

    }

    @Override
    public Mono<String> createUser(UserDto userDto) {
        return repository.save(mapper.mapFrom(userDto))
                .flatMap(user -> Mono.just("User with username "+user.getUsername()+ " has successfully been created"))
                .doOnNext(r -> logger.log(Level.INFO, "Created new customer with username: "+userDto.getUsername()))
                .onErrorResume(DuplicateKeyException.class, ex -> {
                    logger.log(Level.WARNING, "Caught DuplicateKeyException during user creation", ex);
                    return Mono.error(new AlreadyExistsException("User with username: " + userDto.getUsername() + " already exists"));
                });
    }
}
