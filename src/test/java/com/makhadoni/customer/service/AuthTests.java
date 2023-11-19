package com.makhadoni.customer.service;

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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.any;

public class AuthTests {

    @Mock
    public UserRepository userRepository;

    @InjectMocks
    public UserServiceImpl userService;

    private final UserDto userDto = TestSuccessFactory.getUserDto();

    private final User user = TestSuccessFactory.getUser();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void canSuccessfullyRegister() {

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.createUser(userDto))
                .expectNextMatches(dto ->
                        dto.equals("User with username " + userDto.getUsername() + " has successfully been created")
                ).verifyComplete();
    }

    @Test
    public void canSuccessfullyGetUser() {

        Mockito.when(userRepository.findByUsername(any())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.getUser(userDto.getUsername()))
                .expectNextMatches(dto ->
                        dto.getUsername().equals(userDto.getUsername()) &&
                                dto.getPassword().equals(userDto.getPassword())
                ).verifyComplete();
    }
}

