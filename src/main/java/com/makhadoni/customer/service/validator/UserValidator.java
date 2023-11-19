package com.makhadoni.customer.service.validator;

import com.makhadoni.customer.service.modules.auth.dto.UserDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDto userDto = (UserDto) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "field.required." , "username cannot be empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password.required", "Password must not be empty");

        if (userDto.getPassword().length() < 5) {
            errors.rejectValue("password", "password.tooShort", "Password must be at least 5 characters long");
        }

    }
}
