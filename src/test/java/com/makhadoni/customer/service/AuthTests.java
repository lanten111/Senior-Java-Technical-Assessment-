package com.makhadoni.customer.service;

import com.makhadoni.customer.service.cache.UserCacheService;
import com.makhadoni.customer.service.modules.auth.Repository.UserRepository;
import com.makhadoni.customer.service.modules.auth.domain.User;
import com.makhadoni.customer.service.modules.auth.dto.UserDto;
import com.makhadoni.customer.service.modules.auth.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.any;

 class AuthTests {

    @Mock
    public UserRepository userRepository;

     @Mock
     public UserCacheService cacheService;

    @InjectMocks
    public UserServiceImpl userService;

    private final UserDto userDto = TestSuccessFactory.getUserDto();

    private final User user = TestSuccessFactory.getUser();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(userService, "userCacheTimeout", 5);
    }

    @Test
     void canSuccessfullyRegister() {

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.createUser(userDto))
                .expectNextMatches(dto ->
                        dto.equals("User with username " + userDto.getUsername() + " has successfully been created")
                ).verifyComplete();
    }

    @Test
     void canSuccessfullyGetUserFromCache() {

        Mockito.when(cacheService.getValue(any())).thenReturn(Mono.just(userDto));

        StepVerifier.create(userService.getUser(userDto.getUsername()))
                .expectNextMatches(dto ->
                        dto.getUsername().equals(userDto.getUsername()) &&
                                dto.getPassword().equals(userDto.getPassword())
                ).verifyComplete();
    }

     @Test
     void canSuccessfullyGetUserFromRepo() {

         Mockito.when(cacheService.getValue(any())).thenReturn(Mono.empty());
         Mockito.when(userRepository.findByUsername(any())).thenReturn(Mono.just(user));
         Mockito.when(cacheService.setValue(any(), any(), any())).thenReturn(Mono.just(true));

         StepVerifier.create(userService.getUser(userDto.getUsername()))
                 .expectNextMatches(dto ->
                         dto.getUsername().equals(userDto.getUsername()) &&
                                 dto.getPassword().equals(userDto.getPassword())
                 ).verifyComplete();
     }
}

