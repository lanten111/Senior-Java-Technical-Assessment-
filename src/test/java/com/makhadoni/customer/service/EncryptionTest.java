package com.makhadoni.customer.service;

import com.makhadoni.customer.service.encryption.EncryptionService;
import com.makhadoni.customer.service.modules.customer.dto.CustomerDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

public class EncryptionTest {

    @InjectMocks
    public EncryptionService encryptionService;

    private final CustomerDto customerDto = TestSuccessFactory.getCustomerDto();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(encryptionService, "key", "1234567812345678");
        ReflectionTestUtils.setField(encryptionService, "initVector", "1234567812345678");
        ReflectionTestUtils.setField(encryptionService, "algo", "AES/CBC/PKCS5PADDING");
    }

    @Test
    void canSuccessfullyEncryptAndDecrypt() {

        String encryptedEmail = encryptionService.encrypt(customerDto.getEmail());

        Assertions.assertNotNull(encryptedEmail);

        String unencryptedEmail = encryptionService.decrypt(encryptedEmail);

        Assertions.assertEquals(customerDto.getEmail(), unencryptedEmail);
    }
}
