package com.makhadoni.customer.service.validator;

import com.makhadoni.customer.service.exception.ConstraintViolationException;
import com.makhadoni.customer.service.modules.customer.dto.CustomerDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
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
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required." , "firstName cannot be empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "field.required." , "lastName cannot be empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "field.required." , "email cannot be empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "age", "field.required." , "age cannot be empty");
        if ((customerDto.getAge() < 10 || customerDto.getAge() > 120)) {
            errors.rejectValue("age", "field.invalid", "Age must be between 10 and 120");
        }
    }


    public Mono<Integer> getAndValidateQueryParam(ServerRequest request, String queryParam, int defaultValue){
        return Mono.just(request.queryParam(queryParam)
                .map(value -> {
                    if (!isNumeric(value)) {
                        throw new IllegalArgumentException("Invalid query parameter: "+value);
                    } else if (value.isEmpty()) {
                        return defaultValue;
                    } else {
                        return Integer.parseInt(value);
                    }
                }).get());

    }

    public Mono<Integer> getAndValidatePathParam(ServerRequest request, String pathParam){
        return Mono.just(request.pathVariable(pathParam))
                .map(value -> {
                    if (!isPresentAndNumeric(value)){
                        throw new IllegalArgumentException("Invalid or missing required numeric path variable: "+value );
                    } else {
                        return Integer.parseInt(value);
                    }
                });
    }

    private boolean isPresentAndNumeric(String str) {
        return str != null && isNumeric(str);
    }

    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}