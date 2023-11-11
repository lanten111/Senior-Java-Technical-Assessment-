package com.makhadoni.customer.service.router;

import com.makhadoni.customer.service.service.CustomerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CustomerRouterConfig {

    private final CustomerService customerService;

    public CustomerRouterConfig(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Bean
    public RouterFunction<ServerResponse> customerRoute(CustomerRouterHandler handler) {
        return
                RouterFunctions.nest(path("/api/customer"),
                        route(GET(""),handler::getCustomers)
                                .andRoute(GET("/{id}"), handler::getCustomer)
                                .andRoute(POST(""),handler::createCustomer)
                                .andRoute(PATCH(""),handler::updateCustomer)
                                .andRoute(DELETE("/{id}"), handler::deleteCustomer));
    }
}

