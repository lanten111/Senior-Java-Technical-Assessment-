package com.makhadoni.customer.service;

import com.makhadoni.customer.service.modules.auth.dto.UserDto;
import com.makhadoni.customer.service.security.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
/**
 * @author makhado on 2023/11/21
 */
class TokenTest {

    @InjectMocks
    public TokenService tokenService;

    private final UserDto userDto = TestSuccessFactory.getUserDto();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(tokenService, "secret", "IS5IobIItq0pRX9JL9TvdQ40Oa93u2Wojlign4V3L30");
        ReflectionTestUtils.setField(tokenService, "issuer", "GK");
        ReflectionTestUtils.setField(tokenService, "expiresInMinutes", "5");
    }

    @Test
    void canSuccessfullyCreateToken() {

        Map<String, String> token = tokenService.getToken(userDto.getUsername());
        Assertions.assertNotNull(token.get("token"));
    }
}
