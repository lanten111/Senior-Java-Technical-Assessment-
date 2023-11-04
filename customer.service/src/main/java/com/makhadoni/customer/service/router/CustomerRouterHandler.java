package com.makhadoni.customer.service.router;

import com.makhadoni.customer.service.service.CustomerService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class CustomerRouterHandler {

    private final CustomerService customerService;

    public CustomerRouterHandler(CustomerService customerService) {
        this.customerService = customerService;
    }

    public RouterFunction<ServerResponse> customers(){
        return RouterFunctions.route()
                .path("/customer",
                        builder -> builder
                                .GET(""))
    }
}
