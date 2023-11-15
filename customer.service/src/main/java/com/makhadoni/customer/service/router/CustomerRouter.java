package com.makhadoni.customer.service.router;

import com.makhadoni.customer.service.dto.CustomerDto;
import com.makhadoni.customer.service.handler.CustomerRouterHandler;
import com.makhadoni.customer.service.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CustomerRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(path = "/api/customer/?page", produces = {
                            MediaType.APPLICATION_JSON_VALUE},
                                    method = RequestMethod.GET,
                                    beanClass = CustomerService.class,
                                    beanMethod = "getCustomers",
                            operation = @Operation(operationId = "getCustomers",
                                    responses = {
                                        @ApiResponse(responseCode = "200", description = "successful operation",
                                                content = @Content(schema = @Schema(implementation = CustomerDto.class))),
                                        @ApiResponse(responseCode = "400", description = "Invalid customer details supplied"),
                                        @ApiResponse(responseCode = "404", description = "no customer found")},
                                    parameters = {
                                        @Parameter(in = ParameterIn.QUERY, name = "page", required = false),
                                        @Parameter(in = ParameterIn.QUERY, name = "size", required = false),
                                        @Parameter(in = ParameterIn.QUERY, name = "firstName", required = false)})),

                    @RouterOperation(path = "/api/customer/{customerId}", produces = {
                            MediaType.APPLICATION_JSON_VALUE},
                                    method = RequestMethod.GET,
                                    beanClass = CustomerService.class,
                                    beanMethod = "getCustomer",
                            operation = @Operation(operationId = "getCustomer",
                                    responses = {
                                        @ApiResponse(responseCode = "200", description = "successful operation",
                                            content = @Content(schema = @Schema(implementation = CustomerDto.class))),
                                        @ApiResponse(responseCode = "400", description = "Bad parameter input"),
                                        @ApiResponse(responseCode = "404", description = "No customer found")},
                                    parameters = {
                                        @Parameter(in = ParameterIn.PATH, name = "firstName", required = true)})),

                    @RouterOperation(path = "/api/customer", produces = {
                            MediaType.APPLICATION_JSON_VALUE},
                                    method = RequestMethod.POST,
                                    beanClass = CustomerService.class,
                                    beanMethod = "createCustomer",
                            operation = @Operation(operationId = "createCustomer",
                                    responses = {
                                            @ApiResponse(responseCode = "200", description = "successful operation",
                                                    content =
                                                        @Content(
                                                                schema =
                                                                    @Schema(implementation = CustomerDto.class))),
                                            @ApiResponse(responseCode = "409", description = "Customer of the same details already exist")},
                                    requestBody =
                                            @RequestBody(content = @Content(schema = @Schema(implementation = CustomerDto.class))))),

                    @RouterOperation(path = "/api/customer", produces = {
                            MediaType.APPLICATION_JSON_VALUE},
                            method = RequestMethod.PATCH,
                            beanClass = CustomerService.class,
                            beanMethod = "UpdateCustomer",
                            operation = @Operation(operationId = "UpdateCustomer",
                                    responses = {
                                            @ApiResponse(responseCode = "200", description = "successful operation",
                                                    content =
                                                        @Content(schema =
                                                            @Schema(implementation = CustomerDto.class))),
                                            @ApiResponse(responseCode = "409", description = "Customer of the same details already exist")},
                                    requestBody =
                                            @RequestBody(content = @Content(schema = @Schema(implementation = CustomerDto.class))))
                    ),

                    @RouterOperation(path = "/api/customer/{customerId}", produces = {
                            MediaType.APPLICATION_JSON_VALUE},
                                method = RequestMethod.DELETE, beanClass = CustomerService.class, beanMethod = "deleteCustomer",
                            operation = @Operation(operationId = "deleteCustomer",
                                    responses = {
                                            @ApiResponse(responseCode = "204", description = "successful operation", content = @Content(mediaType = "Boolean")),
                                            @ApiResponse(responseCode = "400", description = "Bad parameter input"),},
                                    parameters = {
                                        @Parameter(in = ParameterIn.PATH, name = "customerId")}
                    )),
            })
    public RouterFunction<ServerResponse> customerRoute(CustomerRouterHandler handler) {
        return
                RouterFunctions.nest(path("/api/customer"),
                        route(GET(""),handler::getCustomers)
                                .andRoute(GET("/{customerId}"), handler::getCustomer)
                                .andRoute(POST(""),handler::createCustomer)
                                .andRoute(PATCH(""),handler::updateCustomer)
                                .andRoute(DELETE("/{customerId}"), handler::deleteCustomer));
    }
}


