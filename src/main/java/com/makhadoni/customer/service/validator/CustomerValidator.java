package com.makhadoni.customer.service.validator;

import com.makhadoni.customer.service.modules.customer.dto.CustomerDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class CustomerValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return CustomerDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors)  {

        CustomerDto customerDto = (CustomerDto) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required." , "firstName cannot be empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "field.required." , "lastName cannot be empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "field.required." , "email cannot be empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "age", "field.required." , "age cannot be empty");
        if ((customerDto.getAge() < 10 || customerDto.getAge() > 120)) {
            errors.rejectValue("age", "field.invalid", "Age must be between 10 and 120");
        }
    }
}