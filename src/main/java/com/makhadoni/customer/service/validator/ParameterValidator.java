package com.makhadoni.customer.service.validator;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public class ParameterValidator {

    public static Mono<Integer> getAndValidateQueryParam(ServerRequest request, String queryParam, int defaultValue){
        return Mono.justOrEmpty(request.queryParam(queryParam)).<Integer>handle((value, sink) -> {
            if (!isNumeric(value)) {
                sink.error(new IllegalArgumentException("Invalid query parameter: " + value));
            } else {
                sink.next(Integer.parseInt(value));
            }
        })
                .defaultIfEmpty(defaultValue);

    }

    public static Mono<Integer> getAndValidatePathParam(ServerRequest request, String pathParam){
        return Mono.just(request.pathVariable(pathParam))
                .handle((value, sink) -> {
                    if (!isPresentAndNumeric(value)){
                        sink.error(new IllegalArgumentException("Invalid or missing required numeric path variable: " + value));
                    } else {
                        sink.next(Integer.parseInt(value));
                    }
                });
    }

    private static boolean isPresentAndNumeric(String str) {
        return str != null && isNumeric(str);
    }

    private static boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
