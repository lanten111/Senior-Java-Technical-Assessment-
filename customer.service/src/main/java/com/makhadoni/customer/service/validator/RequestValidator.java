package com.makhadoni.customer.service.validator;

import com.makhadoni.customer.service.dto.CustomerDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import reactor.core.publisher.Mono;

@Component
public class RequestValidator implements Validator {



    @Override
    public boolean supports(Class<?> clazz) {
        return CustomerDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors)  {

        CustomerDto customerDto = (CustomerDto) target;
        // Perform custom validation logic
        // Example: Check if the "name" property is empty
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required." , "Name cannot be empty");

        if ((customerDto.getAge() < 0 || customerDto.getAge() > 120)) {
            errors.rejectValue("age", "field.invalid", "Age must be between 0 and 120");
        }

        // You can add more validation rules as needed
    }



}